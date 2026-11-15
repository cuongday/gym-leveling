package com.gymleveling.user;

import java.time.LocalDate;

/**
 * @param daysUsedThisMonth days paused in the calendar month of the user's current local date
 */
public record PauseInfo(LocalDate startDate, LocalDate endDate, int daysUsedThisMonth) {
}
