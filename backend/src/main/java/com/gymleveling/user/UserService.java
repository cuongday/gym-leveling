package com.gymleveling.user;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

	UserProfile register(String email, String passwordHash, String displayName, String locale, String timezone);

	Optional<UserAccount> findAccountByEmail(String email);

	UserProfile getProfile(UUID id);

	static String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}

}
