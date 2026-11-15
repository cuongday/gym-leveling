package com.gymleveling.auth.internal;

import com.gymleveling.user.UserProfile;

public record TokenResponse(String accessToken, String refreshToken, long expiresIn, String tokenType,
		UserProfile user) {
}
