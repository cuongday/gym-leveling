package com.gymleveling.auth.internal;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;
import com.gymleveling.shared.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class RefreshTokenService {

	private static final SecureRandom RANDOM = new SecureRandom();

	private final RefreshTokenRepository repository;
	private final JwtProperties props;
	private final Clock clock;

	RefreshTokenService(RefreshTokenRepository repository, JwtProperties props, Clock clock) {
		this.repository = repository;
		this.props = props;
		this.clock = clock;
	}

	/** Issues a new refresh token and returns the raw value; only its SHA-256 hex is stored. */
	@Transactional
	String issue(UUID userId, UUID familyId) {
		byte[] bytes = new byte[32];
		RANDOM.nextBytes(bytes);
		String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
		Instant now = clock.instant();
		repository.save(new RefreshTokenEntity(userId, sha256Hex(raw), familyId, now.plus(props.refreshTtl()), now));
		return raw;
	}

	record Rotated(UUID userId, String refreshToken) {
	}

	/**
	 * Rotates a refresh token. noRollbackFor so that the revoke-all on reuse detection commits even though
	 * the 401 is thrown. The row lock serialises concurrent use of the same token.
	 */
	@Transactional(noRollbackFor = ApiException.class)
	Rotated rotate(String rawToken) {
		Instant now = clock.instant();
		RefreshTokenEntity token = repository.findByTokenHashForUpdate(sha256Hex(rawToken))
			.orElseThrow(RefreshTokenService::invalid);
		if (token.getRevokedAt() != null || !token.getExpiresAt().isAfter(now)) {
			throw invalid();
		}
		if (token.getUsedAt() != null) {
			repository.revokeAllForUser(token.getUserId(), now);
			throw invalid();
		}
		token.setUsedAt(now);
		return new Rotated(token.getUserId(), issue(token.getUserId(), token.getFamilyId()));
	}

	/** Revokes every token in the presented token's family; unknown tokens are a no-op. */
	@Transactional
	void revokeFamily(String rawToken) {
		repository.findByTokenHash(sha256Hex(rawToken))
			.ifPresent(t -> repository.revokeFamily(t.getFamilyId(), clock.instant()));
	}

	private static ApiException invalid() {
		return new ApiException(HttpStatus.UNAUTHORIZED, "auth.invalid_refresh_token");
	}

	static String sha256Hex(String value) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

}
