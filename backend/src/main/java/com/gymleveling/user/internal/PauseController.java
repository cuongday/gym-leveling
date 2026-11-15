package com.gymleveling.user.internal;

import com.gymleveling.shared.security.CurrentUser;
import com.gymleveling.user.PauseInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/pause")
class PauseController {

	record PauseRequest(LocalDate startDate, @NotNull @Min(1) @Max(7) Integer days) {
	}

	private final PauseServiceImpl pauses;

	PauseController(PauseServiceImpl pauses) {
		this.pauses = pauses;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	PauseInfo create(@Valid @RequestBody PauseRequest req) {
		return pauses.create(CurrentUser.id(), req.startDate(), req.days());
	}

	@GetMapping
	PauseInfo current() {
		return pauses.current(CurrentUser.id());
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void cancel() {
		pauses.cancel(CurrentUser.id());
	}

}
