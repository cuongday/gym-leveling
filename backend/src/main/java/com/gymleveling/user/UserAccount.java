package com.gymleveling.user;

import java.util.UUID;

public record UserAccount(UUID id, String email, String passwordHash) {
}
