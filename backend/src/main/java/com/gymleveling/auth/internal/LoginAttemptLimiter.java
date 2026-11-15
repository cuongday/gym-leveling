package com.gymleveling.auth.internal;

import com.gymleveling.shared.web.ApiException;
import java.time.Duration;
import java.util.List;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
class LoginAttemptLimiter {

	static final int MAX_FAILURES = 5;
	static final Duration WINDOW = Duration.ofMinutes(15);
	private static final String PREFIX = "auth:login-fail:";

	// Atomic: the window starts at the first failure and the key can never be left without a TTL.
	private static final DefaultRedisScript<Long> INCR_WITH_TTL = new DefaultRedisScript<>(
			"local c=redis.call('INCR',KEYS[1]); if c==1 then redis.call('EXPIRE',KEYS[1],ARGV[1]) end; return c",
			Long.class);

	private final StringRedisTemplate redis;

	LoginAttemptLimiter(StringRedisTemplate redis) {
		this.redis = redis;
	}

	void checkAllowed(String normalizedEmail) {
		String value = redis.opsForValue().get(PREFIX + normalizedEmail);
		if (value != null && Long.parseLong(value) >= MAX_FAILURES) {
			throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "auth.too_many_attempts");
		}
	}

	void recordFailure(String normalizedEmail) {
		String key = PREFIX + normalizedEmail;
		redis.execute(INCR_WITH_TTL, List.of(key), String.valueOf(WINDOW.toSeconds()));
	}

	void reset(String normalizedEmail) {
		redis.delete(PREFIX + normalizedEmail);
	}

}
