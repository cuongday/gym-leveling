package com.gymleveling.user.internal;

import com.gymleveling.shared.web.ApiException;
import com.gymleveling.user.UserTime;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
class UserTimeImpl implements UserTime {

	private final UserRepository repository;
	private final Clock clock;

	UserTimeImpl(UserRepository repository, Clock clock) {
		this.repository = repository;
		this.clock = clock;
	}

	@Override
	public ZoneId zoneAt(UUID userId, Instant at) {
		UserEntity u = repository.findById(userId)
			.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "user.not_found"));
		return ZoneId.of(u.effectiveTimezone(at));
	}

	@Override
	public LocalDate today(UUID userId) {
		Instant now = clock.instant();
		return LocalDate.ofInstant(now, zoneAt(userId, now));
	}

}
