package com.apiintegration.core.utils;

import java.net.URL;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
@Getter
@Service
public class APIDataObject {

	public Object bodyObject;
	public HashMap<String, String> headerPairs;
	public HashMap<String, String> queryParam;

}
