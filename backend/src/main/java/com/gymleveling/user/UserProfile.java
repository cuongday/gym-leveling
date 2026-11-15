package com.gymleveling.user;

import java.time.Instant;
import java.util.UUID;

public record UserProfile(UUID id, String email, String displayName, String locale, String timezone,
		String pendingTimezone, Instant pendingTimezoneFrom, Instant timezoneChangeAvailableAt, boolean premium) {
}
