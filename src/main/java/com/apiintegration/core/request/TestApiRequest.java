package com.apiintegration.core.request;

import com.apiintegration.core.utils.APIDataObject;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
@ToString
public class TestApiRequest {

	private APIDataObject data;
	private Long apiId;
}