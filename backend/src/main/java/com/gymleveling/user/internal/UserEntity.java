package com.gymleveling.user.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "users")
class UserEntity {

	@Id
	@GeneratedValue
	@UuidGenerator
	private UUID id;

	@Column(nullable = false, unique = true, length = 254)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 100)
	private String passwordHash;

	@Column(name = "display_name", nullable = false, length = 40)
	private String displayName;

	@Column(nullable = false, length = 2)
	private String locale;

	@Column(nullable = false, length = 64)
	private String timezone;

	@Column(name = "timezone_changed_at")
	private Instant timezoneChangedAt;

	@Column(name = "pending_timezone", length = 64)
	private String pendingTimezone;

	@Column(name = "pending_timezone_from")
	private Instant pendingTimezoneFrom;

	@Column(name = "premium_until")
	private Instant premiumUntil;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected UserEntity() {
	}

	UserEntity(String email, String passwordHash, String displayName, String locale, String timezone, Instant now) {
		this.email = email;
		this.passwordHash = passwordHash;
		this.displayName = displayName;
		this.locale = locale;
		this.timezone = timezone;
		this.createdAt = now;
		this.updatedAt = now;
	}

	UUID getId() { return id; }
	String getEmail() { return email; }
	String getPasswordHash() { return passwordHash; }
	String getDisplayName() { return displayName; }
	String getLocale() { return locale; }
	String getTimezone() { return timezone; }
	Instant getTimezoneChangedAt() { return timezoneChangedAt; }
	String getPendingTimezone() { return pendingTimezone; }
	Instant getPendingTimezoneFrom() { return pendingTimezoneFrom; }
	Instant getPremiumUntil() { return premiumUntil; }
	Instant getCreatedAt() { return createdAt; }
	Instant getUpdatedAt() { return updatedAt; }

	String effectiveTimezone(Instant at) {
		if (pendingTimezone != null && pendingTimezoneFrom != null && !pendingTimezoneFrom.isAfter(at)) {
			return pendingTimezone;
		}
		return timezone;
	}

	/** Folds an elapsed pending timezone into the current one. */
	void foldPending(Instant now) {
		if (pendingTimezone != null && pendingTimezoneFrom != null && !pendingTimezoneFrom.isAfter(now)) {
			timezone = pendingTimezone;
			pendingTimezone = null;
			pendingTimezoneFrom = null;
		}
	}

	void setPending(String tz, Instant from, Instant now) {
		this.pendingTimezone = tz;
		this.pendingTimezoneFrom = from;
		this.timezoneChangedAt = now;
	}

	void clearPending() {
		this.pendingTimezone = null;
		this.pendingTimezoneFrom = null;
	}

	void setDisplayName(String displayName) { this.displayName = displayName; }
	void setLocale(String locale) { this.locale = locale; }
	void touch(Instant now) { this.updatedAt = now; }

}
