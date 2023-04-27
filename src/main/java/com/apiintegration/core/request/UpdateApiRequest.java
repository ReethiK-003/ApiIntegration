package com.apiintegration.core.request;

import java.util.HashMap;

import javax.validation.constraints.NotNull;

import com.apiintegration.core.utils.api.Auth;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
@ToString
public class UpdateApiRequest {

	@NotNull(message = "api_id should not blank !!")
	private Long id;

	private String apiName;

	private String apiMethod;

	private String apiUrl;

	private Object apiBody;

	private HashMap<String, String> apiHeader;

	private HashMap<String, String> apiQueryParam;

	private Auth apiAuth;

}