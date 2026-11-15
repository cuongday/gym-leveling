package com.gymleveling.user;

import java.time.LocalDate;
import java.util.UUID;

/** Pause-mode queries for other modules (streak, point deduction). */
public interface PauseService {

	boolean isPaused(UUID userId, LocalDate date);

}
