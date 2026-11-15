package com.gymleveling.shared.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gymleveling.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class PingControllerTest {

	static final String EN = "Hello from Gym Leveling";
	static final String VI = "Xin chào từ Gym Leveling";

	@Autowired
	MockMvc mvc;

	private void expectMessage(String acceptLanguage, String expected) throws Exception {
		var req = get("/api/v1/ping");
		if (acceptLanguage != null) {
			req = req.header("Accept-Language", acceptLanguage);
		}
		mvc.perform(req)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("ok"))
			.andExpect(jsonPath("$.message").value(expected));
	}

	@Test
	void ping_english() throws Exception {
		expectMessage("en", EN);
	}

	@Test
	void ping_vietnamese() throws Exception {
		expectMessage("vi", VI);
	}

	@Test
	void ping_vietnameseRegionTag() throws Exception {
		expectMessage("vi-VN,vi;q=0.9", VI);
	}

	@Test
	void ping_fallsBackToEnglish() throws Exception {
		expectMessage("fr", EN);
		expectMessage(null, EN);
	}

	@Test
	void unknownRoute_returnsProblemDetail() throws Exception {
		mvc.perform(get("/api/v1/does-not-exist").with(jwt()))
			.andExpect(status().isNotFound())
			.andExpect(content().contentTypeCompatibleWith("application/problem+json"))
			.andExpect(jsonPath("$.status").value(404));
	}

}
