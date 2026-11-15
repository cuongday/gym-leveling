package com.gymleveling.shared.web;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.lang.Nullable;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

	private final MessageSource messages;

	public ApiExceptionHandler(MessageSource messages) {
		this.messages = messages;
	}

	/** Builds a coded problem with a detail translated for the current locale. */
	public static ProblemDetail problem(MessageSource messages, HttpStatus status, String code, Object... args) {
		String detail = messages.getMessage("error." + code, args, code, LocaleContextHolder.getLocale());
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
		pd.setTitle(status.getReasonPhrase());
		pd.setProperty("code", code);
		return pd;
	}

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ProblemDetail> handleApi(ApiException ex) {
		ProblemDetail pd = problem(messages, ex.getStatus(), ex.getCode(), ex.getArgs());
		if ("validation.failed".equals(ex.getCode())) {
			pd.setProperty("errors", List.of());
		}
		return ResponseEntity.status(ex.getStatus()).headers(ex.getHeaders()).body(pd);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		ProblemDetail pd = problem(messages, HttpStatus.BAD_REQUEST, "validation.failed");
		List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
			.map(fe -> Map.of("field", fe.getField(),
					"message", String.valueOf(fe.getDefaultMessage())))
			.toList();
		pd.setProperty("errors", errors);
		return ResponseEntity.badRequest().body(pd);
	}

	/** Fallback: log server-side, never leak the exception message to the client. */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex) throws Exception {
		if (ex instanceof AccessDeniedException || ex instanceof AuthenticationException) {
			throw ex; // let Spring Security's handlers deal with these
		}
		log.error("Unhandled exception", ex);
		HttpStatus st = HttpStatus.INTERNAL_SERVER_ERROR;
		return ResponseEntity.status(st).body(problem(messages, st, "internal.error"));
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body,
			HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		if (body == null && ex instanceof org.springframework.web.ErrorResponse er) {
			body = er.getBody();
		}
		if (body instanceof ProblemDetail pd && (pd.getProperties() == null || !pd.getProperties().containsKey("code"))) {
			String code;
			if (ex instanceof HttpMessageNotReadableException) {
				code = "validation.failed";
			}
			else if (ex instanceof NoResourceFoundException || ex instanceof NoHandlerFoundException) {
				code = "not_found";
			}
			else if (statusCode.is5xxServerError()) {
				code = "internal.error";
			}
			else {
				code = "request.invalid";
			}
			pd.setDetail(messages.getMessage("error." + code, null, code, LocaleContextHolder.getLocale()));
			pd.setProperty("code", code);
			if ("validation.failed".equals(code)) {
				pd.setProperty("errors", List.of());
			}
		}
		return super.handleExceptionInternal(ex, body, headers, statusCode, request);
	}

}
