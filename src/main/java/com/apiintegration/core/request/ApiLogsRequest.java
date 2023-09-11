package com.apiintegration.core.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Setter
@Getter
public class ApiLogsRequest {
	
	private Long apiId;
	
	private Integer pageNo;
	
	private Integer pageSize;
	
	private String order;
	
	private String field;
}
