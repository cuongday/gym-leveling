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
