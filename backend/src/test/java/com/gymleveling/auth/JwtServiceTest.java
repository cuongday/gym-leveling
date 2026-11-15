package com.gymleveling.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gymleveling.MutableClock;
import com.gymleveling.auth.internal.JwtProperties;
import com.gymleveling.auth.internal.JwtService;
import jakarta.validation.Validation;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

class JwtServiceTest {

	static final String SECRET = "test-secret-test-secret-test-secret-0123";

	final MutableClock clock = new MutableClock();
	final JwtProperties props = new JwtProperties(SECRET, "gym-leveling", Duration.ofMinutes(15), Duration.ofDays(30));
	final JwtService service = new JwtService(props, clock);
	final JwtDecoder decoder = service.jwtDecoder();

	@Test
	void issuedToken_decodesWithSubjectIssuerAndExpiry() {
		UUID id = UUID.randomUUID();
		Jwt jwt = decoder.decode(service.issueAccessToken(id));
		assertThat(jwt.getSubject()).isEqualTo(id.toString());
		assertThat(jwt.getClaimAsString("iss")).isEqualTo("gym-leveling");
		assertThat(Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt())).isEqualTo(Duration.ofSeconds(900));
	}

	@Test
	void expiredToken_rejected() {
		String token = service.issueAccessToken(UUID.randomUUID());
		clock.advance(Duration.ofMinutes(16));
		assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class);
	}

	@Test
	void shortSecret_failsValidation() {
		var validator = Validation.buildDefaultValidatorFactory().getValidator();
		var shortProps = new JwtProperties("x".repeat(31), "gym-leveling", Duration.ofMinutes(15), Duration.ofDays(30));
		assertThat(validator.validate(shortProps)).isNotEmpty();
		assertThat(validator.validate(props)).isEmpty();
	}

	@Test
	void wrongIssuer_rejected() {
		var other = new JwtService(new JwtProperties(SECRET, "someone-else", Duration.ofMinutes(15), Duration.ofDays(30)), clock);
		String token = other.issueAccessToken(UUID.randomUUID());
		assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class);
	}

	@Test
	void differentSecret_rejected() {
		var other = new JwtService(new JwtProperties("another-secret-another-secret-another-0123", "gym-leveling",
				Duration.ofMinutes(15), Duration.ofDays(30)), clock);
		String token = other.issueAccessToken(UUID.randomUUID());
		assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class);
	}

	@Test
	void unsignedAlgNoneToken_rejected() {
		var enc = Base64.getUrlEncoder().withoutPadding();
		long exp = clock.instant().getEpochSecond() + 900;
		String header = enc.encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8));
		String payload = enc.encodeToString(("{\"sub\":\"" + UUID.randomUUID() + "\",\"iss\":\"gym-leveling\",\"exp\":"
				+ exp + "}").getBytes(StandardCharsets.UTF_8));
		String token = header + "." + payload + ".";
		assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class);
	}

	@Test
	void toString_masksSecret() {
		assertThat(props.toString()).doesNotContain(SECRET);
	}

}
