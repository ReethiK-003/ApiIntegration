package com.apiintegration.core.model.service;

import java.util.HashMap;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter
public class HashMapToDbJsonConverter implements AttributeConverter<HashMap<String, String>, String> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(HashMap<String, String> attribute) {
		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error converting HashMap to JSON", e);
		}
	}

	@Override
	public HashMap<String, String> convertToEntityAttribute(String dbData) {
		try {
			return objectMapper.readValue(dbData, new TypeReference<HashMap<String, String>>() {
			});
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error converting JSON to HashMap", e);
		}
	}
}