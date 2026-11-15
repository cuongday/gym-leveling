package com.gymleveling.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gymleveling.MutableClock;
import com.gymleveling.TestClockConfiguration;
import com.gymleveling.TestcontainersConfiguration;
import com.jayway.jsonpath.JsonPath;
import java.util.Set;
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
class AuthControllerTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	StringRedisTemplate redis;

	@Autowired
	MutableClock clock;

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

	private ResultActions register(String email, String password, String name, String locale) throws Exception {
		return mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"email":"%s","password":"%s","displayName":"%s","locale":"%s"}
					""".formatted(email, password, name, locale)));
	}

	private ResultActions login(String email, String password) throws Exception {
		return mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"email":"%s","password":"%s"}
					""".formatted(email, password)));
	}

	@Test
	void register_returns201WithTokensAndProfile() throws Exception {
		String body = register("a@b.com", "password1", "Alice", "en").andExpect(status().isCreated())
			.andExpect(jsonPath("$.accessToken").isNotEmpty())
			.andExpect(jsonPath("$.refreshToken").isNotEmpty())
			.andExpect(jsonPath("$.tokenType").value("Bearer"))
			.andExpect(jsonPath("$.expiresIn").value(900))
			.andExpect(jsonPath("$.user.email").value("a@b.com"))
			.andReturn()
			.getResponse()
			.getContentAsString();
		String token = JsonPath.read(body, "$.accessToken");
		mvc.perform(get("/api/v1/ping").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
		Integer rows = jdbc.queryForObject("select count(*) from refresh_tokens", Integer.class);
		assertThat(rows).isEqualTo(1);
	}

	@Test
	void register_emailNormalized_duplicateRejected() throws Exception {
		register(" A@B.com ", "password1", "Alice", "en").andExpect(status().isCreated());
		register("a@b.com", "password1", "Alice", "en").andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value("auth.email_taken"));
	}

	@Test
	void register_invalidInput_returns400WithFieldErrors() throws Exception {
		register("a@b.com", "1234567", "", "fr").andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("validation.failed"))
			.andExpect(jsonPath("$.errors[*].field").value(hasItems("password", "displayName", "locale")));
	}

	@Test
	void login_emailCaseInsensitive() throws Exception {
		register("a@b.com", "password1", "Alice", "en").andExpect(status().isCreated());
		login("A@B.COM", "password1").andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").isNotEmpty());
	}

	@Test
	void login_wrongPassword_returns401Generic() throws Exception {
		register("a@b.com", "password1", "Alice", "en").andExpect(status().isCreated());
		String wrong = login("a@b.com", "badpassword").andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("auth.invalid_credentials"))
			.andReturn().getResponse().getContentAsString();
		String unknown = login("nobody@b.com", "badpassword").andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("auth.invalid_credentials"))
			.andReturn().getResponse().getContentAsString();
		assertThat((String) JsonPath.read(wrong, "$.detail")).isEqualTo(JsonPath.read(unknown, "$.detail"));
	}

	@Test
	void login_unknownEmail_returns401Generic() throws Exception {
		login("nobody@b.com", "password1").andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("auth.invalid_credentials"))
			.andExpect(jsonPath("$.detail").value("Email or password is incorrect."));
	}

	@Test
	void login_fiveFailures_thenLocked() throws Exception {
		register("a@b.com", "password1", "Alice", "en").andExpect(status().isCreated());
		for (int i = 0; i < 5; i++) {
			login("a@b.com", "badpassword").andExpect(status().isUnauthorized());
		}
		login("a@b.com", "password1").andExpect(status().isTooManyRequests())
			.andExpect(jsonPath("$.code").value("auth.too_many_attempts"));
	}

	@Test
	void login_success_resetsFailureCounter() throws Exception {
		register("a@b.com", "password1", "Alice", "en").andExpect(status().isCreated());
		for (int i = 0; i < 4; i++) {
			login("a@b.com", "badpassword").andExpect(status().isUnauthorized());
		}
		login("a@b.com", "password1").andExpect(status().isOk());
		for (int i = 0; i < 4; i++) {
			login("a@b.com", "badpassword").andExpect(status().isUnauthorized());
		}
	}

}
