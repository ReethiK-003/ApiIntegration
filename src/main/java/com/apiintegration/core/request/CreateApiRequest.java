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

//	public HashMap<String, String> getApiHeader() throws JsonMappingException, JsonProcessingException {
//		String jsonHeader = objectMapper.writeValueAsString(this.apiHeader);
//		return new ObjectMapper().readValue(jsonHeader, new TypeReference<HashMap<String, String>>() {
//		});
//	}
//
//	public HashMap<String, String> getApiQueryParam() throws JsonMappingException, JsonProcessingException {
//		String jsonQueryParam = objectMapper.writeValueAsString(this.apiQueryParam);
//		return new ObjectMapper().readValue(jsonQueryParam, new TypeReference<HashMap<String, String>>() {
//		});
//	}
//
//	public Auth getApiAUth() throws JsonMappingException, JsonProcessingException {
//		
//		return new ObjectMapper().readValue(this.apiAuth.toString(), Auth.class);
//	}

}