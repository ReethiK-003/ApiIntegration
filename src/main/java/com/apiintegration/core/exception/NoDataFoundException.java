package com.apiintegration.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NO_CONTENT)
public class NoDataFoundException extends EntryNotFoundException {

	public NoDataFoundException(String msg) {
		super(msg);
	}

}