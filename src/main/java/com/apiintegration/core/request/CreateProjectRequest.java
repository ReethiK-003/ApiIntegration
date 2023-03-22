package com.apiintegration.core.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
@ToString
public class CreateProjectRequest {

	@NotBlank(message = "Project Name should not be blank !!")
	@Pattern(regexp = "^[a-zA-Z]([a-zA-Z0-9_-])+$", message = "Project name should start with alpgabets and only be in AlphaNumeric with Underscore and numbers ")
	private String projectName;

	@NotBlank(message = "Project Description should not be blank !!")
	private String projectDescription;

}