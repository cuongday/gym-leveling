package com.gymleveling.user.internal;

import com.gymleveling.shared.web.ApiException;
import com.gymleveling.user.UserAccount;
import com.gymleveling.user.UserProfile;
import com.gymleveling.user.UserService;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Override
	@Transactional
	public UserProfile updateProfile(UUID id, String displayName, String locale, String timezone) {
		String newTz = timezone == null ? null : validateTimezone(timezone);
		String name = null;
		if (displayName != null) {
			name = displayName.trim();
			if (name.isEmpty() || name.length() > 40) {
				throw new ApiException(HttpStatus.BAD_REQUEST, "validation.failed");
			}
		}
		if (locale != null && !locale.equals("vi") && !locale.equals("en")) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "validation.failed");
		}
		UserEntity u = repository.findById(id)
			.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "user.not_found"));
		Instant now = clock.instant();
		u.foldPending(now);
		if (name != null) {
			u.setDisplayName(name);
		}
		if (locale != null) {
			u.setLocale(locale);
		}
		if (newTz != null) {
			applyTimezoneChange(u, newTz, now);
		}
		u.touch(now);
		return toProfile(repository.saveAndFlush(u));
	}

	private void applyTimezoneChange(UserEntity u, String newTz, Instant now) {
		if (newTz.equals(u.getTimezone())) {
			if (u.getPendingTimezone() != null) {
				u.clearPending();
			}
			return;
		}
		if (newTz.equals(u.getPendingTimezone())) {
			return;
		}
		Instant changedAt = u.getTimezoneChangedAt();
		if (changedAt != null && now.isBefore(changedAt.plus(TIMEZONE_CHANGE_INTERVAL))) {
			throw new ApiException(HttpStatus.CONFLICT, "user.timezone_change_too_soon");
		}
		ZoneId zone = ZoneId.of(u.getTimezone());
		Instant from = LocalDate.ofInstant(now, zone).plusDays(1).atStartOfDay(zone).toInstant();
		u.setPending(newTz, from, now);
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
		String tz = u.effectiveTimezone(now);
		String pending = null;
		Instant pendingFrom = null;
		if (u.getPendingTimezone() != null && u.getPendingTimezoneFrom() != null
				&& u.getPendingTimezoneFrom().isAfter(now)) {
			pending = u.getPendingTimezone();
			pendingFrom = u.getPendingTimezoneFrom();
		}
		Instant availableAt = null;
		if (u.getTimezoneChangedAt() != null) {
			Instant next = u.getTimezoneChangedAt().plus(TIMEZONE_CHANGE_INTERVAL);
			if (next.isAfter(now)) {
				availableAt = next;
			}
		}
		boolean premium = u.getPremiumUntil() != null && u.getPremiumUntil().isAfter(now);
		return new UserProfile(u.getId(), u.getEmail(), u.getDisplayName(), u.getLocale(), tz,
				pending, pendingFrom, availableAt, premium);
	}

}
