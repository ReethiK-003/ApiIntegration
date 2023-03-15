package com.apiintegration.core.request;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
@ToString
public class SignupRequest {

	@NotBlank(message = "Email should not be blank")
	@Email(message = "Invalid Email format")
	public String email;

	@NotBlank(message = "First name should not be blank")
	public String firstName;

	@NotBlank(message = "Last name should not be blank")
	public String lastName;
	
	@NotBlank
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,16}$", message = "Password must be 8-16 characters in length")
	public CharSequence password;
	
	@NotBlank
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,16}$", message = "Password must ")
	public CharSequence confirmPassword;

	@AssertTrue(message = "You must accept terms and conditions")
	public boolean terms;

}
