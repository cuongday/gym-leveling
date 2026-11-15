package com.gymleveling.user.internal;

import com.gymleveling.shared.web.ApiException;
import com.gymleveling.user.UserAccount;
import com.gymleveling.user.UserProfile;
import com.gymleveling.user.UserService;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
class UserServiceImpl implements UserService {

	static final String DEFAULT_TIMEZONE = "Asia/Ho_Chi_Minh";
	static final Duration TIMEZONE_CHANGE_INTERVAL = Duration.ofDays(30);

	private final UserRepository repository;
	private final Clock clock;

	UserServiceImpl(UserRepository repository, Clock clock) {
		this.repository = repository;
		this.clock = clock;
	}

	@Override
	public UserProfile register(String email, String passwordHash, String displayName, String locale,
			String timezone) {
		String normalized = UserService.normalizeEmail(email);
		String tz = timezone == null ? DEFAULT_TIMEZONE : validateTimezone(timezone);
		if (repository.existsByEmail(normalized)) {
			throw emailTaken();
		}
		UserEntity entity = new UserEntity(normalized, passwordHash, displayName, locale, tz, clock.instant());
		try {
			return toProfile(repository.saveAndFlush(entity));
		}
		catch (DataIntegrityViolationException e) {
			throw emailTaken();
		}
	}

	@Override
	public Optional<UserAccount> findAccountByEmail(String email) {
		return repository.findByEmail(UserService.normalizeEmail(email))
			.map(u -> new UserAccount(u.getId(), u.getEmail(), u.getPasswordHash()));
	}

	@Override
	public UserProfile getProfile(UUID id) {
		return repository.findById(id)
			.map(this::toProfile)
			.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "user.not_found"));
	}

	private static ApiException emailTaken() {
		return new ApiException(HttpStatus.CONFLICT, "auth.email_taken");
	}

	private static String validateTimezone(String tz) {
		try {
			ZoneId id = ZoneId.of(tz);
			if (!tz.equals(id.getId()) || tz.startsWith("+") || tz.startsWith("-") || tz.equals("Z")) {
				throw new DateTimeException("offset");
			}
			return tz;
		}
		catch (DateTimeException e) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "user.invalid_timezone");
		}
	}

	private UserProfile toProfile(UserEntity u) {
		Instant now = clock.instant();
		Instant availableAt = null;
		if (u.getTimezoneChangedAt() != null) {
			Instant next = u.getTimezoneChangedAt().plus(TIMEZONE_CHANGE_INTERVAL);
			if (next.isAfter(now)) {
				availableAt = next;
			}
		}
		boolean premium = u.getPremiumUntil() != null && u.getPremiumUntil().isAfter(now);
		return new UserProfile(u.getId(), u.getEmail(), u.getDisplayName(), u.getLocale(), u.getTimezone(),
				u.getPendingTimezone(), u.getPendingTimezoneFrom(), availableAt, premium);
	}

}
