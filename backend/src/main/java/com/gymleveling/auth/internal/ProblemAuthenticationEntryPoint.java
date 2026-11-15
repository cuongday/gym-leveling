package com.gymleveling.auth.internal;

import com.gymleveling.shared.web.ApiExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class ProblemAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final MessageSource messages;
	private final JsonMapper mapper;

	public ProblemAuthenticationEntryPoint(MessageSource messages, JsonMapper mapper) {
		this.messages = messages;
		this.mapper = mapper;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		ProblemDetail pd = ApiExceptionHandler.problem(messages, HttpStatus.UNAUTHORIZED, "auth.unauthorized");
		pd.setInstance(java.net.URI.create(request.getRequestURI()));
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setHeader("WWW-Authenticate", "Bearer");
		response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		mapper.writeValue(response.getOutputStream(), pd);
	}

}
