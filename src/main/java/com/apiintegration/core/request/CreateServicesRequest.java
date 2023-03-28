package com.apiintegration.core.request;

import java.net.URI;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
@ToString
public class CreateServicesRequest {
	
	@NotBlank(message = "Service name should not be blank !!")
	@Pattern(regexp = "^[a-zA-Z]([a-zA-Z0-9_-])+$", message = "Project name should start with alpgabets and only be in AlphaNumeric with Underscore and numbers ")
	private String serviceName;
	
	@NonNull
	private Long projectId;

	@NonNull
	private URI baseUrl;
	
	private URI baseUrlLive;
	
	private boolean isLive;
	
}


