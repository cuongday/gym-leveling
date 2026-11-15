package com.gymleveling.user.internal;

import com.gymleveling.shared.web.ApiException;
import com.gymleveling.user.PauseInfo;
import com.gymleveling.user.PauseService;
import com.gymleveling.user.UserTime;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class PauseServiceImpl implements PauseService {

	static final int MONTHLY_LIMIT = 7;
	static final int MAX_DAYS_AHEAD = 30;

	private final PausePeriodRepository periods;
	private final UserRepository users;
	private final UserTime userTime;
	private final Clock clock;

	PauseServiceImpl(PausePeriodRepository periods, UserRepository users, UserTime userTime, Clock clock) {
		this.periods = periods;
		this.users = users;
		this.userTime = userTime;
		this.clock = clock;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isPaused(UUID userId, LocalDate date) {
		return periods.existsByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, date, date);
	}

	@Transactional
	PauseInfo create(UUID userId, LocalDate requestedStart, int days) {
		users.findByIdForUpdate(userId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "user.not_found"));
		LocalDate today = userTime.today(userId);
		LocalDate start = requestedStart == null ? today : requestedStart;
		if (start.isBefore(today) || start.isAfter(today.plusDays(MAX_DAYS_AHEAD))) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "pause.invalid_dates");
		}
		if (periods.findFirstByUserIdAndEndDateGreaterThanEqualOrderByStartDateAsc(userId, today).isPresent()) {
			throw new ApiException(HttpStatus.CONFLICT, "pause.already_scheduled");
		}
		LocalDate end = start.plusDays(days - 1L);
		for (YearMonth m = YearMonth.from(start); !m.isAfter(YearMonth.from(end)); m = m.plusMonths(1)) {
			LocalDate from = m.atDay(1);
			LocalDate to = m.atEndOfMonth();
			int used = daysIn(periods.findByUserIdAndEndDateGreaterThanEqualAndStartDateLessThanEqual(userId, from, to),
					from, to);
			int added = overlap(start, end, from, to);
			if (used + added > MONTHLY_LIMIT) {
				throw new ApiException(HttpStatus.CONFLICT, "pause.limit_exceeded");
			}
		}
		periods.save(new PausePeriodEntity(userId, start, end, clock.instant()));
		return new PauseInfo(start, end, usedInMonth(userId, today));
	}

	@Transactional(readOnly = true)
	PauseInfo current(UUID userId) {
		LocalDate today = userTime.today(userId);
		PausePeriodEntity p = periods.findFirstByUserIdAndEndDateGreaterThanEqualOrderByStartDateAsc(userId, today)
			.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "pause.none_active"));
		return new PauseInfo(p.getStartDate(), p.getEndDate(), usedInMonth(userId, today));
	}

	@Transactional
	void cancel(UUID userId) {
		users.findByIdForUpdate(userId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "user.not_found"));
		LocalDate today = userTime.today(userId);
		PausePeriodEntity p = periods.findFirstByUserIdAndEndDateGreaterThanEqualOrderByStartDateAsc(userId, today)
			.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "pause.none_active"));
		if (p.getStartDate().isBefore(today)) {
			p.setEndDate(today.minusDays(1));
		}
		else {
			periods.delete(p);
		}
	}

	private int usedInMonth(UUID userId, LocalDate day) {
		YearMonth m = YearMonth.from(day);
		LocalDate from = m.atDay(1);
		LocalDate to = m.atEndOfMonth();
		return daysIn(periods.findByUserIdAndEndDateGreaterThanEqualAndStartDateLessThanEqual(userId, from, to), from,
				to);
	}

	private static int daysIn(List<PausePeriodEntity> list, LocalDate from, LocalDate to) {
		return list.stream().mapToInt(p -> overlap(p.getStartDate(), p.getEndDate(), from, to)).sum();
	}

	private static int overlap(LocalDate s1, LocalDate e1, LocalDate s2, LocalDate e2) {
		LocalDate s = s1.isAfter(s2) ? s1 : s2;
		LocalDate e = e1.isBefore(e2) ? e1 : e2;
		return e.isBefore(s) ? 0 : (int) (e.toEpochDay() - s.toEpochDay() + 1);
	}

}
