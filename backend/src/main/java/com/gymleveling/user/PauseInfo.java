package com.gymleveling.user;

import java.time.LocalDate;

public record PauseInfo(LocalDate startDate, LocalDate endDate, int daysUsedThisMonth) {
}
