package com.gymleveling;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class MutableClock extends Clock {

	public static final Instant DEFAULT_INSTANT = Instant.parse("2026-11-16T03:00:00Z");

	private volatile Instant instant = DEFAULT_INSTANT;

	public void setInstant(Instant instant) {
		this.instant = instant;
	}

	public void advance(Duration duration) {
		this.instant = this.instant.plus(duration);
	}

	@Override
	public ZoneId getZone() {
		return ZoneOffset.UTC;
	}

	@Override
	public Clock withZone(ZoneId zone) {
		return this;
	}

	@Override
	public Instant instant() {
		return instant;
	}

}
