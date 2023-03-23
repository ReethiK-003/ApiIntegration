package com.apiintegration.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.NoArgsConstructor;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
@NoArgsConstructor
public class EntryNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EntryNotFoundException(String msg) {
		super(msg);
	}

	public EntryNotFoundException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}