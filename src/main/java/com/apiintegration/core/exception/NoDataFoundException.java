package com.apiintegration.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NO_CONTENT)
public class NoDataFoundException extends EntryNotFoundException {

	private static final long serialVersionUID = 805602034515321048L;

	public NoDataFoundException(String msg) {
		super(msg);
	}

}