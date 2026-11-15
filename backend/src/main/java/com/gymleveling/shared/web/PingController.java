package com.gymleveling.shared.web;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
class PingController {

	record PingResponse(String status, String message) {
	}

	private final MessageSource messages;

	PingController(MessageSource messages) {
		this.messages = messages;
	}

	@GetMapping("/ping")
	PingResponse ping() {
		return new PingResponse("ok", messages.getMessage("ping.message", null, LocaleContextHolder.getLocale()));
	}

}
