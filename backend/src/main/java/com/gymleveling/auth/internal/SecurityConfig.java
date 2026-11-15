package com.gymleveling.auth.internal;

import jakarta.servlet.DispatcherType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JwtProperties.class)
class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, ProblemAuthenticationEntryPoint entryPoint)
			throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(a -> a
				.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
				.requestMatchers("/api/v1/auth/**", "/api/v1/ping", "/actuator/health", "/v3/api-docs/**",
						"/swagger-ui/**", "/swagger-ui.html")
				.permitAll()
				.anyRequest().authenticated())
			.exceptionHandling(e -> e.authenticationEntryPoint(entryPoint))
			.oauth2ResourceServer(o -> o.bearerTokenResolver(bearerTokenResolver())
				.authenticationEntryPoint(entryPoint)
				.jwt(j -> {
				}));
		return http.build();
	}

	/**
	 * Clients attach their stored (possibly expired) access token to every request; public routes must ignore
	 * it, otherwise an expired token would make /auth/refresh itself fail with 401.
	 */
	static BearerTokenResolver bearerTokenResolver() {
		DefaultBearerTokenResolver delegate = new DefaultBearerTokenResolver();
		return request -> {
			String path = request.getRequestURI().substring(request.getContextPath().length());
			if (path.startsWith("/api/v1/auth/") || path.equals("/api/v1/ping") || path.equals("/actuator/health")) {
				return null;
			}
			return delegate.resolve(request);
		};
	}

}
