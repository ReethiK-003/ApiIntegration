package com.apiintegration.core.request;

import java.util.HashMap;
import com.apiintegration.core.utils.api.Auth;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
public class CreateApiRequest {

	@JsonIgnore
	private final ObjectMapper objectMapper = new ObjectMapper();

	private Long serviceId;

	private String apiName;

	private String apiMethod;

	private String apiUrl;

	private Object apiBody;

	private HashMap<String, String> apiHeader;

	private HashMap<String, String> apiQueryParam;

	private Auth apiAuth;

}
