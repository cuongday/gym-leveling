package com.gymleveling.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.gymleveling.MutableClock;
import com.gymleveling.TestClockConfiguration;
import com.gymleveling.TestcontainersConfiguration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@Import({ TestcontainersConfiguration.class, TestClockConfiguration.class })
class UserTimeTest {

	@Autowired
	UserTime userTime;

	@Autowired
	UserService users;

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	MutableClock clock;

	@BeforeEach
	void clean() {
		clock.setInstant(MutableClock.DEFAULT_INSTANT);
		jdbc.update("delete from refresh_tokens");
		jdbc.update("delete from pause_periods");
		jdbc.update("delete from users");
	}

	@Test
	void zoneAt_usesPendingOnlyFromEffectiveInstant() {
		UUID id = users.register("t@b.com", "x", "T", "en", "Asia/Ho_Chi_Minh").id();
		Instant from = Instant.parse("2026-11-20T00:00:00Z");
		jdbc.update("update users set pending_timezone='Europe/Paris', pending_timezone_from=? where id=?",
				java.sql.Timestamp.from(from), id);
		assertThat(userTime.zoneAt(id, from.minusSeconds(1))).isEqualTo(ZoneId.of("Asia/Ho_Chi_Minh"));
		assertThat(userTime.zoneAt(id, from)).isEqualTo(ZoneId.of("Europe/Paris"));
	}

	@Test
	void today_usesClockAndZone() {
		UUID id = users.register("t@b.com", "x", "T", "en", "Asia/Ho_Chi_Minh").id();
		clock.setInstant(Instant.parse("2026-11-16T17:30:00Z"));
		assertThat(userTime.today(id)).isEqualTo(LocalDate.parse("2026-11-17"));
	}

}
