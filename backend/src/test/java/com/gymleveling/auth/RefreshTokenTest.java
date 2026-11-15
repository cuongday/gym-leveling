package com.gymleveling.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gymleveling.MutableClock;
import com.gymleveling.TestClockConfiguration;
import com.gymleveling.TestcontainersConfiguration;
import com.jayway.jsonpath.JsonPath;
import java.time.Duration;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@Import({ TestcontainersConfiguration.class, TestClockConfiguration.class })
class RefreshTokenTest {

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

	private void register() throws Exception {
		mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"email":"a@b.com","password":"password1","displayName":"Alice","locale":"en"}
					""")).andExpect(status().isCreated());
	}

	private String login() throws Exception {
		String body = mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"email":"a@b.com","password":"password1"}
					""")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		return JsonPath.read(body, "$.refreshToken");
	}

	private ResultActions refresh(String token) throws Exception {
		return mvc.perform(post("/api/v1/auth/refresh").contentType(MediaType.APPLICATION_JSON)
			.content("{\"refreshToken\":\"%s\"}".formatted(token)));
	}

	private ResultActions logout(String token) throws Exception {
		return mvc.perform(post("/api/v1/auth/logout").contentType(MediaType.APPLICATION_JSON)
			.content("{\"refreshToken\":\"%s\"}".formatted(token)));
	}

	private String refreshOk(String token) throws Exception {
		String body = refresh(token).andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").isNotEmpty())
			.andExpect(jsonPath("$.user.email").value("a@b.com"))
			.andReturn().getResponse().getContentAsString();
		return JsonPath.read(body, "$.refreshToken");
	}

	@Test
	void refresh_rotatesToken() throws Exception {
		register();
		String r1 = login();
		String r2 = refreshOk(r1);
		assertThat(r2).isNotEqualTo(r1);
		refreshOk(r2);
	}

	@Test
	void refresh_reuseRevokesAllTokens() throws Exception {
		register();
		String r1 = login();
		String s1 = login();
		String r2 = refreshOk(r1);
		clock.advance(Duration.ofSeconds(11));
		refresh(r1).andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("auth.invalid_refresh_token"));
		refresh(r2).andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("auth.invalid_refresh_token"));
		refresh(s1).andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("auth.invalid_refresh_token"));
		Integer active = jdbc.queryForObject("select count(*) from refresh_tokens where revoked_at is null",
				Integer.class);
		assertThat(active).isZero();
	}

	@Test
	void refresh_reuseWithinGrace_doesNotRevoke() throws Exception {
		register();
		String r1 = login();
		String r2 = refreshOk(r1);
		refresh(r1).andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("auth.invalid_refresh_token"));
		refreshOk(r2);
	}

	@Test
	void refresh_reuseExactlyAtGraceBoundary_revokes() throws Exception {
		register();
		String r1 = login();
		String s1 = login();
		String r2 = refreshOk(r1);
		clock.advance(Duration.ofSeconds(10));
		refresh(r1).andExpect(status().isUnauthorized());
		refresh(r2).andExpect(status().isUnauthorized());
		refresh(s1).andExpect(status().isUnauthorized());
	}

	@Test
	void refresh_expiresExactlyNow_returns401() throws Exception {
		register();
		String r1 = login();
		clock.advance(Duration.ofDays(30));
		refresh(r1).andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("auth.invalid_refresh_token"));
	}

	@Test
	void refresh_expired_returns401() throws Exception {
		register();
		String r1 = login();
		clock.advance(Duration.ofDays(30).plusSeconds(1));
		refresh(r1).andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("auth.invalid_refresh_token"));
	}

	@Test
	void logout_revokesFamily_isIdempotent() throws Exception {
		register();
		String r1 = login();
		logout(r1).andExpect(status().isNoContent());
		refresh(r1).andExpect(status().isUnauthorized());
		logout(r1).andExpect(status().isNoContent());
		logout("garbage").andExpect(status().isNoContent());
	}

	@Test
	void logout_doesNotAffectOtherDevices() throws Exception {
		register();
		String r1 = login();
		String s1 = login();
		logout(r1).andExpect(status().isNoContent());
		refreshOk(s1);
	}

	@Test
	void refresh_concurrentSameToken_onlyOneSucceeds() throws Exception {
		register();
		String r1 = login();
		ExecutorService pool = Executors.newFixedThreadPool(2);
		CountDownLatch start = new CountDownLatch(1);
		List<Future<String[]>> results = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			results.add(pool.submit(() -> {
				start.await();
				MockHttpServletResponse res = refresh(r1).andReturn().getResponse();
				return new String[] { String.valueOf(res.getStatus()), res.getContentAsString() };
			}));
		}
		start.countDown();
		List<Integer> statuses = new ArrayList<>();
		String winnerToken = null;
		for (Future<String[]> f : results) {
			String[] r = f.get();
			statuses.add(Integer.parseInt(r[0]));
			if (r[0].equals("200")) {
				winnerToken = JsonPath.read(r[1], "$.refreshToken");
			}
		}
		pool.shutdown();
		assertThat(statuses).containsExactlyInAnyOrder(200, 401);
		refreshOk(winnerToken);
	}

	@Test
	void refresh_withExpiredAccessTokenHeader_stillSucceeds() throws Exception {
		register();
		String body = mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"email":"a@b.com","password":"password1"}
					""")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		String access = JsonPath.read(body, "$.accessToken");
		String r1 = JsonPath.read(body, "$.refreshToken");
		clock.advance(Duration.ofMinutes(16));
		mvc.perform(post("/api/v1/auth/refresh").header("Authorization", "Bearer " + access)
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"refreshToken\":\"%s\"}".formatted(r1)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").isNotEmpty());
		mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + access))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("auth.unauthorized"));
	}

}
