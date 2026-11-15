package com.gymleveling.user;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Answers "what day is it for this user" for other modules.
 * <p>
 * An unknown user id → {@link com.gymleveling.shared.web.ApiException} 404 {@code user.not_found}.
 */
public interface UserTime {

	ZoneId zoneAt(UUID userId, Instant at);

	LocalDate today(UUID userId);

}
