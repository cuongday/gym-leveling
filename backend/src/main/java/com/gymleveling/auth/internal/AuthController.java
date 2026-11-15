package com.gymleveling.auth.internal;

import com.gymleveling.shared.web.ApiException;
import com.gymleveling.user.UserAccount;
import com.gymleveling.user.UserProfile;
import com.gymleveling.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Intentionally NOT @Transactional: UserService.register handles the duplicate-email race itself.
@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

	record RegisterRequest(
			@NotBlank @Email @Size(max = 254) String email,
			@NotBlank @Size(min = 8, max = 72) @MaxBytes(72) String password,
			@NotBlank @Size(max = 40) String displayName,
			@NotBlank @Pattern(regexp = "vi|en") String locale,
			String timezone) {

		RegisterRequest {
			email = email == null ? null : email.trim();
			displayName = displayName == null ? null : displayName.trim();
		}

	}

	record LoginRequest(@NotBlank String email, @NotBlank String password) {

		LoginRequest {
			email = email == null ? null : email.trim();
		}

	}

	record RefreshRequest(@NotBlank String refreshToken) {
	}

	private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	private final String dummyHash = passwordEncoder.encode("dummy-password-for-timing");

	private final UserService userService;
	private final JwtService jwtService;
	private final JwtProperties props;
	private final RefreshTokenService refreshTokens;
	private final LoginAttemptLimiter limiter;

	AuthController(UserService userService, JwtService jwtService, JwtProperties props,
			RefreshTokenService refreshTokens, LoginAttemptLimiter limiter) {
		this.userService = userService;
		this.jwtService = jwtService;
		this.props = props;
		this.refreshTokens = refreshTokens;
		this.limiter = limiter;
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	TokenResponse register(@Valid @RequestBody RegisterRequest req) {
		// The user is committed before the refresh token is issued (deliberately non-transactional so the
		// duplicate-email race stays 409); if issuing fails the user can simply log in.
		UserProfile profile = userService.register(req.email(), passwordEncoder.encode(req.password()),
				req.displayName(), req.locale(), req.timezone());
		return tokens(profile);
	}

	@PostMapping("/login")
	TokenResponse login(@Valid @RequestBody LoginRequest req) {
		String email = UserService.normalizeEmail(req.email());
		limiter.checkAllowed(email);
		UserAccount account = userService.findAccountByEmail(email).orElse(null);
		boolean ok = passwordEncoder.matches(req.password(), account != null ? account.passwordHash() : dummyHash);
		if (account == null || !ok) {
			limiter.recordFailure(email);
			throw new ApiException(HttpStatus.UNAUTHORIZED, "auth.invalid_credentials");
		}
		limiter.reset(email);
		return tokens(userService.getProfile(account.id()));
	}

	@PostMapping("/refresh")
	TokenResponse refresh(@Valid @RequestBody RefreshRequest req) {
		RefreshTokenService.Rotated rotated = refreshTokens.rotate(req.refreshToken());
		UserProfile profile = userService.getProfile(rotated.userId());
		return new TokenResponse(jwtService.issueAccessToken(rotated.userId()), rotated.refreshToken(),
				props.accessTtl().toSeconds(), "Bearer", profile);
	}

	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void logout(@Valid @RequestBody RefreshRequest req) {
		refreshTokens.revokeFamily(req.refreshToken());
	}

	private TokenResponse tokens(UserProfile profile) {
		UUID id = profile.id();
		String refresh = refreshTokens.issue(id, UUID.randomUUID());
		return new TokenResponse(jwtService.issueAccessToken(id), refresh, props.accessTtl().toSeconds(), "Bearer",
				profile);
	}

}
