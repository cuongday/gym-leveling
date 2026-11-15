package com.gymleveling.user.internal;

import com.gymleveling.shared.security.CurrentUser;
import com.gymleveling.user.UserProfile;
import com.gymleveling.user.UserService;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/me")
class MeController {

	record UpdateProfileRequest(String displayName, @Pattern(regexp = "vi|en") String locale, String timezone) {
	}

	private final UserService users;

	MeController(UserService users) {
		this.users = users;
	}

	@GetMapping
	UserProfile me() {
		return users.getProfile(CurrentUser.id());
	}

	@PatchMapping
	UserProfile update(@Valid @RequestBody UpdateProfileRequest req) {
		return users.updateProfile(CurrentUser.id(), req.displayName(), req.locale(), req.timezone());
	}

}
