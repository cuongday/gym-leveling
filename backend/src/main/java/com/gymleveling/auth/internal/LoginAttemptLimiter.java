package com.gymleveling.auth.internal;

import com.gymleveling.shared.web.ApiException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
class LoginAttemptLimiter {

	static final int MAX_FAILURES = 5;
	static final Duration WINDOW = Duration.ofMinutes(15);
	private static final String PREFIX = "auth:login-fail:";

	// Atomic: the window starts at the first attempt and the key can never be left without a TTL.
	private static final DefaultRedisScript<Long> INCR_WITH_TTL = new DefaultRedisScript<>(
			"local c=redis.call('INCR',KEYS[1]); if c==1 then redis.call('EXPIRE',KEYS[1],ARGV[1]) end; return c",
			Long.class);

	private final StringRedisTemplate redis;

	LoginAttemptLimiter(StringRedisTemplate redis) {
		this.redis = redis;
	}

	/**
	 * Counts this attempt atomically BEFORE the password is checked, so parallel guesses cannot slip past the
	 * limit. Attempts 1..5 proceed; the 6th and later within the window get 429. A successful login calls
	 * {@link #reset}, so only failures accumulate in practice.
	 */
	void acquire(String normalizedEmail) {
		String key = PREFIX + normalizedEmail;
		Long count = redis.execute(INCR_WITH_TTL, List.of(key), String.valueOf(WINDOW.toSeconds()));
		if (count != null && count > MAX_FAILURES) {
			Long ttl = redis.getExpire(key, TimeUnit.SECONDS);
			long retryAfter = ttl != null && ttl > 0 ? ttl : WINDOW.toSeconds();
			throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "auth.too_many_attempts")
				.withHeader("Retry-After", String.valueOf(retryAfter));
		}
	}

	void reset(String normalizedEmail) {
		redis.delete(PREFIX + normalizedEmail);
	}

}
