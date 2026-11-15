package com.gymleveling.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gymleveling.MutableClock;
import com.gymleveling.TestClockConfiguration;
import com.gymleveling.TestcontainersConfiguration;
import com.jayway.jsonpath.JsonPath;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@Import({ TestcontainersConfiguration.class, TestClockConfiguration.class })
class PauseTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	StringRedisTemplate redis;

	@Autowired
	MutableClock clock;

	@Autowired
	UserTime userTime;

	@Autowired
	PauseService pauses;

	private UUID userId;

	@BeforeEach
	void clean() throws Exception {
		clock.setInstant(MutableClock.DEFAULT_INSTANT);
		jdbc.update("delete from refresh_tokens");
		jdbc.update("delete from pause_periods");
		jdbc.update("delete from users");
		Set<String> keys = redis.keys("auth:login-fail:*");
		if (keys != null && !keys.isEmpty()) {
			redis.delete(keys);
		}
		String body = mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"email":"p@b.com","password":"password1","displayName":"Alice","locale":"en"}
					"""))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();
		userId = UUID.fromString(JsonPath.read(body, "$.user.id"));
	}

	private String token() throws Exception {
		String body = mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"email":"p@b.com","password":"password1"}
					"""))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		return "Bearer " + JsonPath.read(body, "$.accessToken");
	}

	private ResultActions pause(String json) throws Exception {
		return mvc.perform(post("/api/v1/me/pause").header("Authorization", token())
			.contentType(MediaType.APPLICATION_JSON)
			.content(json));
	}

	private ResultActions cancel() throws Exception {
		return mvc.perform(delete("/api/v1/me/pause").header("Authorization", token()));
	}

	@Test
	void pause_startToday_isPaused() throws Exception {
		pause("{\"days\":3}").andExpect(status().isCreated())
			.andExpect(jsonPath("$.startDate").value("2026-11-16"))
			.andExpect(jsonPath("$.endDate").value("2026-11-18"))
			.andExpect(jsonPath("$.daysUsedThisMonth").value(3));
		LocalDate today = userTime.today(userId);
		assertThat(pauses.isPaused(userId, today)).isTrue();
		assertThat(pauses.isPaused(userId, today.plusDays(2))).isTrue();
		assertThat(pauses.isPaused(userId, today.plusDays(3))).isFalse();
		assertThat(pauses.isPaused(userId, today.minusDays(1))).isFalse();
		mvc.perform(get("/api/v1/me/pause").header("Authorization", token()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.endDate").value("2026-11-18"));
	}

	@Test
	void get_andDelete_withNone_return404() throws Exception {
		mvc.perform(get("/api/v1/me/pause").header("Authorization", token()))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("pause.none_active"));
		cancel().andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("pause.none_active"));
	}

	@Test
	void pause_secondWhileScheduled_rejected() throws Exception {
		pause("{\"days\":2}").andExpect(status().isCreated());
		pause("{\"days\":1}").andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value("pause.already_scheduled"));
	}

	@Test
	void pause_monthlyLimit() throws Exception {
		pause("{\"days\":5}").andExpect(status().isCreated());
		clock.advance(Duration.ofDays(5)); // 21 Nov, pause is over
		pause("{\"days\":3}").andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value("pause.limit_exceeded"));
		pause("{\"days\":2}").andExpect(status().isCreated()).andExpect(jsonPath("$.daysUsedThisMonth").value(7));
	}

	@Test
	void pause_spanningMonths_countsPerMonth() throws Exception {
		clock.setInstant(Instant.parse("2027-01-20T03:00:00Z"));
		pause("{\"days\":3}").andExpect(status().isCreated());
		clock.advance(Duration.ofDays(3)); // 23 Jan
		pause("{\"startDate\":\"2027-01-28\",\"days\":6}").andExpect(status().isCreated())
			.andExpect(jsonPath("$.startDate").value("2027-01-28"))
			.andExpect(jsonPath("$.endDate").value("2027-02-02"))
			.andExpect(jsonPath("$.daysUsedThisMonth").value(7));
	}

	@Test
	void pause_spanningMonths_overLimitInOneMonth() throws Exception {
		clock.setInstant(Instant.parse("2027-01-20T03:00:00Z"));
		pause("{\"days\":4}").andExpect(status().isCreated());
		clock.advance(Duration.ofDays(4));
		pause("{\"startDate\":\"2027-01-28\",\"days\":6}").andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value("pause.limit_exceeded"));
	}

	@Test
	void pause_cancelFuture_doesNotCount() throws Exception {
		pause("{\"startDate\":\"2026-11-21\",\"days\":3}").andExpect(status().isCreated());
		cancel().andExpect(status().isNoContent());
		assertThat(jdbc.queryForObject("select count(*) from pause_periods", Integer.class)).isZero();
		pause("{\"days\":7}").andExpect(status().isCreated());
	}

	@Test
	void pause_cancelOngoing_keepsElapsedDays() throws Exception {
		pause("{\"days\":5}").andExpect(status().isCreated());
		clock.advance(Duration.ofDays(2)); // 18 Nov
		cancel().andExpect(status().isNoContent());
		assertThat(jdbc.queryForObject("select end_date from pause_periods", LocalDate.class))
			.isEqualTo(LocalDate.parse("2026-11-17"));
		pause("{\"days\":6}").andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value("pause.limit_exceeded"));
		pause("{\"days\":5}").andExpect(status().isCreated());
	}

	@Test
	void pause_cancelStartedToday_deletesRow() throws Exception {
		pause("{\"days\":3}").andExpect(status().isCreated());
		cancel().andExpect(status().isNoContent());
		assertThat(jdbc.queryForObject("select count(*) from pause_periods", Integer.class)).isZero();
	}

	@Test
	void pause_invalidDates_rejected() throws Exception {
		pause("{\"startDate\":\"2026-11-15\",\"days\":2}").andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("pause.invalid_dates"));
		pause("{\"startDate\":\"2026-12-17\",\"days\":2}").andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("pause.invalid_dates"));
		pause("{\"startDate\":\"2026-12-16\",\"days\":2}").andExpect(status().isCreated());
	}

	@Test
	void pause_invalidDays_rejected() throws Exception {
		pause("{\"days\":8}").andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("validation.failed"));
		pause("{\"days\":0}").andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("validation.failed"));
		pause("{}").andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("validation.failed"));
	}

	@Test
	void pause_withoutToken_returns401() throws Exception {
		mvc.perform(post("/api/v1/me/pause").contentType(MediaType.APPLICATION_JSON).content("{\"days\":1}"))
			.andExpect(status().isUnauthorized());
	}

}
