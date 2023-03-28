package com.apiintegration.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.EXPECTATION_FAILED)
public class InvalidOperationException extends RuntimeException {

	private static final long serialVersionUID = 8779288517846958848L;

	public InvalidOperationException(String msg) {
		super(msg);
	}
}