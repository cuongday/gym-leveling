package com.gymleveling.auth.internal;

import jakarta.servlet.DispatcherType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
			.oauth2ResourceServer(o -> o.authenticationEntryPoint(entryPoint).jwt(j -> {
			}));
		return http.build();
	}

}
