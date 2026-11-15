package com.gymleveling.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Import({ TestcontainersConfiguration.class, TestClockConfiguration.class })
class MeControllerTest {

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

	@BeforeEach
	void clean() {
		clock.setInstant(MutableClock.DEFAULT_INSTANT);
		jdbc.update("delete from refresh_tokens");
		jdbc.update("delete from pause_periods");
		jdbc.update("delete from users");
		Set<String> keys = redis.keys("auth:login-fail:*");
		if (keys != null && !keys.isEmpty()) {
			redis.delete(keys);
		}
	}

	private record Session(String token, UUID id) {
	}

	private int counter = 0;

	private Session register(String tz) throws Exception {
		String email = "u" + (counter++) + "@b.com";
		String tzPart = tz == null ? "" : ",\"timezone\":\"" + tz + "\"";
		String body = mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"email":"%s","password":"password1","displayName":"Alice","locale":"en"%s}
					""".formatted(email, tzPart)))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();
		return new Session(JsonPath.read(body, "$.accessToken"), UUID.fromString(JsonPath.read(body, "$.user.id")));
	}

	/** Fresh token for the user at the current clock instant. */
	private String freshToken(Session s) throws Exception {
		String email = jdbc.queryForObject("select email from users where id = ?", String.class, s.id());
		String body = mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"email":"%s","password":"password1"}
					""".formatted(email)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		return JsonPath.read(body, "$.accessToken");
	}

	private ResultActions patchMe(String token, String json) throws Exception {
		return mvc.perform(patch("/api/v1/me").header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content(json));
	}

	@Test
	void me_returnsProfile() throws Exception {
		Session s = register(null);
		mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + s.token()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value(jdbc.queryForObject("select email from users", String.class)))
			.andExpect(jsonPath("$.displayName").value("Alice"))
			.andExpect(jsonPath("$.timezone").value("Asia/Ho_Chi_Minh"))
			.andExpect(jsonPath("$.pendingTimezone").doesNotExist());
	}

	@Test
	void me_withoutToken_returns401() throws Exception {
		mvc.perform(get("/api/v1/me")).andExpect(status().isUnauthorized());
	}

	@Test
	void patch_updatesDisplayNameAndLocale() throws Exception {
		Session s = register(null);
		patchMe(s.token(), """
				{"displayName":"  Bob  ","locale":"vi"}
				""").andExpect(status().isOk())
			.andExpect(jsonPath("$.displayName").value("Bob"))
			.andExpect(jsonPath("$.locale").value("vi"));
		patchMe(s.token(), """
				{"locale":"fr"}
				""").andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("validation.failed"));
		patchMe(s.token(), """
				{"displayName":"   "}
				""").andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("validation.failed"));
	}

	@Test
	void timezoneChange_appliesFromNextLocalMidnight() throws Exception {
		Session s = register("Asia/Ho_Chi_Minh");
		clock.setInstant(Instant.parse("2026-11-16T16:30:00Z"));
		String token = freshToken(s);
		patchMe(token, """
				{"timezone":"Europe/Paris"}
				""").andExpect(status().isOk())
			.andExpect(jsonPath("$.pendingTimezone").value("Europe/Paris"))
			.andExpect(jsonPath("$.pendingTimezoneFrom").value("2026-11-16T17:00:00Z"))
			.andExpect(jsonPath("$.timezone").value("Asia/Ho_Chi_Minh"));
		assertThat(userTime.today(s.id())).isEqualTo(LocalDate.parse("2026-11-16"));

		clock.setInstant(Instant.parse("2026-11-16T17:00:00Z"));
		token = freshToken(s);
		mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.timezone").value("Europe/Paris"))
			.andExpect(jsonPath("$.pendingTimezone").doesNotExist());
		assertThat(userTime.today(s.id())).isEqualTo(LocalDate.parse("2026-11-16"));
	}

	@Test
	void timezoneChange_secondWithin30Days_rejected() throws Exception {
		Session s = register(null);
		patchMe(s.token(), """
				{"timezone":"Europe/Paris"}
				""").andExpect(status().isOk());
		clock.advance(Duration.ofDays(29));
		patchMe(freshToken(s), """
				{"timezone":"Asia/Tokyo"}
				""").andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value("user.timezone_change_too_soon"));
		clock.advance(Duration.ofDays(1));
		patchMe(freshToken(s), """
				{"timezone":"Asia/Tokyo"}
				""").andExpect(status().isOk());
	}

	@Test
	void timezoneChange_firstSetAtRegister_doesNotCount() throws Exception {
		Session s = register("Europe/Paris");
		patchMe(s.token(), """
				{"timezone":"Asia/Tokyo"}
				""").andExpect(status().isOk());
	}

	@Test
	void timezoneChange_backToCurrentClearsPending_stillCounts() throws Exception {
		Session s = register(null);
		patchMe(s.token(), """
				{"timezone":"Europe/Paris"}
				""").andExpect(status().isOk());
		patchMe(s.token(), """
				{"timezone":"Europe/Paris"}
				""").andExpect(status().isOk()).andExpect(jsonPath("$.pendingTimezone").value("Europe/Paris"));
		patchMe(s.token(), """
				{"timezone":"Asia/Ho_Chi_Minh"}
				""").andExpect(status().isOk()).andExpect(jsonPath("$.pendingTimezone").doesNotExist());
		patchMe(s.token(), """
				{"timezone":"Asia/Tokyo"}
				""").andExpect(status().isConflict());
	}

	@Test
	void invalidTimezone_returns400() throws Exception {
		Session s = register(null);
		patchMe(s.token(), """
				{"timezone":"Mars/Base"}
				""").andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("user.invalid_timezone"));
	}

}
