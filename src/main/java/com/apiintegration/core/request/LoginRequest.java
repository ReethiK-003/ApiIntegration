package com.apiintegration.core.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Setter
@Getter
public class LoginRequest {

	@NotBlank(message = "Email Should not be blank")
	@Email(message = "Email is of invalid format")
	private String email;

	@NotBlank(message = "You must enter a password")
	private CharSequence password;

}