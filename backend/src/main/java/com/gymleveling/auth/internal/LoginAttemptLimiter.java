package com.gymleveling.auth.internal;

import com.gymleveling.shared.web.ApiException;
import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
class LoginAttemptLimiter {

	static final int MAX_FAILURES = 5;
	static final Duration WINDOW = Duration.ofMinutes(15);
	private static final String PREFIX = "auth:login-fail:";

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
		Long count = redis.opsForValue().increment(key);
		if (count != null && count == 1L) {
			redis.expire(key, WINDOW);
		}
	}

	void reset(String normalizedEmail) {
		redis.delete(PREFIX + normalizedEmail);
	}

}
