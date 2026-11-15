package com.gymleveling.shared.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

	private final HttpStatus status;
	private final String code;
	private final transient Object[] args;
	private final transient HttpHeaders headers = new HttpHeaders();

	public ApiException(HttpStatus status, String code, Object... args) {
		super(code);
		this.status = status;
		this.code = code;
		this.args = args;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getCode() {
		return code;
	}

	public Object[] getArgs() {
		return args;
	}

	/** Adds a response header (e.g. {@code Retry-After}) to the problem response. */
	public ApiException withHeader(String name, String value) {
		headers.set(name, value);
		return this;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

}
