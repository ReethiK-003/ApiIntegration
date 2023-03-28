package com.apiintegration.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.EXPECTATION_FAILED)
public class InvalidOperationException extends RuntimeException{

	public InvalidOperationException(String msg) {
		super(msg);
	}
}
