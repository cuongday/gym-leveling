package com.gymleveling.user.internal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface PausePeriodRepository extends JpaRepository<PausePeriodEntity, UUID> {

	Optional<PausePeriodEntity> findFirstByUserIdAndEndDateGreaterThanEqualOrderByStartDateAsc(UUID userId,
			LocalDate date);

	boolean existsByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(UUID userId, LocalDate start,
			LocalDate end);

	List<PausePeriodEntity> findByUserIdAndEndDateGreaterThanEqualAndStartDateLessThanEqual(UUID userId,
			LocalDate from, LocalDate to);

}
