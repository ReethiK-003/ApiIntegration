package com.apiintegration.core.utils;

import java.net.URI;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
@ToString
@AllArgsConstructor
public class ApiResponseObject {

	public URI url;
	public Object responseBody;
	public Object responseHeaders;
	public int status;

}
