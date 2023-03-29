package com.apiintegration.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javassist.NotFoundException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserNotFoundException extends NotFoundException {

	private static final long serialVersionUID = -1161139703895552963L;

	public UserNotFoundException(String msg) {
		super(msg);
	}

	public UserNotFoundException() {
		super("User not found with provided Credentials !!");
	}
}