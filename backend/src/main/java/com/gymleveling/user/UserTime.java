package com.gymleveling.user;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

/** Answers "what day is it for this user" for other modules. */
public interface UserTime {

	ZoneId zoneAt(UUID userId, Instant at);

	LocalDate today(UUID userId);

}
