package com.apiintegration.core.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
@ToString
public class VerifyLoginRequest {

	@NotBlank(message = "Email Should not be blank")
	@Email(message = "Email is of invalid format")
	private String email;

	@NotBlank(message = "You must enter a password")
	private CharSequence password;

	@NotBlank(message = "Enter the 2FA code")
	private String code;
}