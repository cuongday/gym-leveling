package com.gymleveling.shared.web;

import java.util.List;
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
		return ResponseEntity.status(ex.getStatus())
			.body(problem(messages, ex.getStatus(), ex.getCode(), ex.getArgs()));
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

}
