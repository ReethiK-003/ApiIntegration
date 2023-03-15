package com.apiintegration.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.EXPECTATION_FAILED)
public class DuplicateEntryException extends RuntimeException {

	public DuplicateEntryException (String msg){
		super (msg);
	}
}
