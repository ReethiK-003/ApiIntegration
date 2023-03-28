package com.apiintegration.core.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChangePasswordRequest {

	@NotBlank
	private CharSequence currentPassword;

	@NotBlank
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,16}$", message = "Password must be 8-16 characters in length")
	private CharSequence newPassword;

	@NotBlank
	private CharSequence confirmPassword;
}