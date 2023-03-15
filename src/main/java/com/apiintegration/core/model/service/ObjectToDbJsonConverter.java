package com.apiintegration.core.model.service;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter
public class ObjectToDbJsonConverter implements AttributeConverter<Object, String> {
	
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(Object attribute) {
		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error converting object to JSON", e);
		}
	}

	@Override
	public Object convertToEntityAttribute(String dbData) {
		try {
			return objectMapper.readValue(dbData, Object.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error converting JSON to object", e);
		}
	}
}