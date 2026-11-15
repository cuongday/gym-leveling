package com.gymleveling.user;

import java.util.UUID;

public record UserAccount(UUID id, String email, String passwordHash) {

	/** Omits the password hash so it can never leak through logs. */
	@Override
	public String toString() {
		return "UserAccount[id=" + id + ", email=" + email + "]";
	}

}
