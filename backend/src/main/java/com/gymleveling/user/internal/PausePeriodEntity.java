package com.gymleveling.user.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "pause_periods")
class PausePeriodEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected PausePeriodEntity() {
	}

	PausePeriodEntity(UUID userId, LocalDate startDate, LocalDate endDate, Instant createdAt) {
		this.userId = userId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.createdAt = createdAt;
	}

	UUID getId() {
		return id;
	}

	LocalDate getStartDate() {
		return startDate;
	}

	LocalDate getEndDate() {
		return endDate;
	}

	void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

}
