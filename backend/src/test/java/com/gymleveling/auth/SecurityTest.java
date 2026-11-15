package com.gymleveling.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gymleveling.MutableClock;
import com.gymleveling.TestClockConfiguration;
import com.gymleveling.TestcontainersConfiguration;
import com.gymleveling.auth.internal.JwtService;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@Import({ TestcontainersConfiguration.class, TestClockConfiguration.class, SecurityTest.BoomConfig.class })
class SecurityTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	JwtService jwtService;

	@Autowired
	MutableClock clock;

	@BeforeEach
	void resetClock() {
		clock.setInstant(MutableClock.DEFAULT_INSTANT);
	}

	@Test
	void ping_stillPublic() throws Exception {
		mvc.perform(get("/api/v1/ping")).andExpect(status().isOk());
	}

	@Test
	void protectedEndpoint_noToken_returns401Problem() throws Exception {
		expectUnauthorized(mvc.perform(get("/api/v1/me").header("Accept-Language", "vi")));
		mvc.perform(get("/api/v1/me"))
			.andExpect(jsonPath("$.detail").value("Please sign in to continue."));
	}

	@Test
	void protectedEndpoint_expiredToken_returns401Problem() throws Exception {
		String token = jwtService.issueAccessToken(UUID.randomUUID());
		clock.advance(Duration.ofMinutes(16));
		expectUnauthorized(mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + token)
			.header("Accept-Language", "vi")));
	}

	@Test
	void protectedEndpoint_tamperedToken_returns401Problem() throws Exception {
		String token = jwtService.issueAccessToken(UUID.randomUUID());
		int i = token.length() - 10; // inside the signature, not the unused trailing bits
		char c = token.charAt(i);
		String tampered = token.substring(0, i) + (c == 'A' ? 'B' : 'A') + token.substring(i + 1);
		expectUnauthorized(mvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + tampered)
			.header("Accept-Language", "vi")));
	}

	private void expectUnauthorized(ResultActions result) throws Exception {
		result.andExpect(status().isUnauthorized())
			.andExpect(content().contentTypeCompatibleWith("application/problem+json"))
			.andExpect(jsonPath("$.code").value("auth.unauthorized"))
			.andExpect(jsonPath("$.status").value(401))
			.andExpect(jsonPath("$.detail").value("Vui lòng đăng nhập để tiếp tục."));
	}

	@Test
	void unexpectedException_returns500ProblemWithoutLeaking() throws Exception {
		var body = mvc.perform(get("/api/v1/auth/__boom").header("Accept-Language", "vi"))
			.andExpect(status().isInternalServerError())
			.andExpect(content().contentTypeCompatibleWith("application/problem+json"))
			.andExpect(jsonPath("$.code").value("internal.error"))
			.andExpect(jsonPath("$.detail").value("Đã có lỗi xảy ra. Vui lòng thử lại sau."))
			.andReturn().getResponse().getContentAsString();
		assertThat(body).doesNotContain("secret detail");
	}

	@TestConfiguration(proxyBeanMethods = false)
	static class BoomConfig {

		@Bean
		BoomController boomController() {
			return new BoomController();
		}

	}

	@RestController
	static class BoomController {

		@GetMapping("/api/v1/auth/__boom")
		String boom() {
			throw new RuntimeException("secret detail");
		}

	}

}
