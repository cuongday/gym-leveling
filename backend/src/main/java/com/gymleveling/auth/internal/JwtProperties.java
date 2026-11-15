package com.gymleveling.auth.internal;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("app.jwt")
public record JwtProperties(
		@NotBlank String secret,
		@DefaultValue("gym-leveling") String issuer,
		@DefaultValue("15m") Duration accessTtl,
		@DefaultValue("30d") Duration refreshTtl) {

	@AssertTrue(message = "app.jwt.secret must be at least 32 bytes (UTF-8)")
	public boolean isSecretLongEnough() {
		return secret != null && secret.getBytes(StandardCharsets.UTF_8).length >= 32;
	}

	@Override
	public String toString() {
		return "JwtProperties[secret=****, issuer=" + issuer + ", accessTtl=" + accessTtl
				+ ", refreshTtl=" + refreshTtl + "]";
	}

}
