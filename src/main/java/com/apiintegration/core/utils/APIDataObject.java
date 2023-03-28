package com.apiintegration.core.utils;

import java.util.HashMap;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
@Getter
@Setter
public class APIDataObject {

	public Object bodyObject;
	public HashMap<String, String> headerPairs;
	public HashMap<String, String> queryParam;

}
