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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

	@Test
	void register_passwordOver72Bytes_returns400() throws Exception {
		register("a@b.com", "ệ".repeat(30), "Alice", "en").andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("validation.failed"))
			.andExpect(jsonPath("$.errors[*].field").value(hasItems("password")));
	}

	@Test
	void register_displayNameTrimmed() throws Exception {
		register("a@b.com", "password1", "  Cường  ", "en").andExpect(status().isCreated())
			.andExpect(jsonPath("$.user.displayName").value("Cường"));
	}

	@Test
	void login_unknownEmailFailuresCountTowardLimit() throws Exception {
		for (int i = 0; i < 5; i++) {
			login("nobody@b.com", "badpassword").andExpect(status().isUnauthorized());
		}
		login("nobody@b.com", "badpassword").andExpect(status().isTooManyRequests())
			.andExpect(jsonPath("$.code").value("auth.too_many_attempts"));
	}

	@Test
	void login_mixedCaseFailuresShareCounter() throws Exception {
		register("a@b.com", "password1", "Alice", "en").andExpect(status().isCreated());
		for (int i = 0; i < 5; i++) {
			login(i % 2 == 0 ? "A@B.com" : "a@b.com", "badpassword").andExpect(status().isUnauthorized());
		}
		login("a@b.com", "password1").andExpect(status().isTooManyRequests())
			.andExpect(jsonPath("$.code").value("auth.too_many_attempts"));
	}

	@Test
	void login_parallelAttempts_capped() throws Exception {
		register("a@b.com", "password1", "Alice", "en").andExpect(status().isCreated());
		int n = 10;
		ExecutorService pool = Executors.newFixedThreadPool(n);
		CountDownLatch start = new CountDownLatch(1);
		List<Future<Integer>> results = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			results.add(pool.submit(() -> {
				start.await();
				return login("a@b.com", "badpassword").andReturn().getResponse().getStatus();
			}));
		}
		start.countDown();
		List<Integer> statuses = new ArrayList<>();
		for (Future<Integer> f : results) {
			statuses.add(f.get());
		}
		pool.shutdown();
		assertThat(statuses).allMatch(s -> s == 401 || s == 429);
		assertThat(statuses.stream().filter(s -> s == 401).count()).isLessThanOrEqualTo(5);
		assertThat(statuses.stream().filter(s -> s == 429).count()).isGreaterThanOrEqualTo(5);
	}

	@Test
	void login_locked_hasRetryAfterHeader() throws Exception {
		for (int i = 0; i < 5; i++) {
			login("nobody@b.com", "badpassword").andExpect(status().isUnauthorized());
		}
		String retryAfter = login("nobody@b.com", "badpassword").andExpect(status().isTooManyRequests())
			.andReturn().getResponse().getHeader("Retry-After");
		assertThat(retryAfter).isNotNull();
		assertThat(Long.parseLong(retryAfter)).isBetween(1L, 900L);
	}

	@Test
	void login_emailOver254_returns400() throws Exception {
		login("a".repeat(250) + "@b.com", "password1").andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("validation.failed"))
			.andExpect(jsonPath("$.errors[*].field").value(hasItems("email")));
	}

}
