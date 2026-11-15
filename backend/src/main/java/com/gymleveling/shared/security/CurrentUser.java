package com.gymleveling.shared.security;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public final class CurrentUser {

	private CurrentUser() {
	}

	public static UUID id() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof JwtAuthenticationToken jwt) {
			return UUID.fromString(jwt.getToken().getSubject());
		}
		throw new IllegalStateException("No authenticated user");
	}

}
