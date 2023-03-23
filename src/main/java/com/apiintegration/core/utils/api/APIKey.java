package com.apiintegration.core.utils.api;

import java.util.HashMap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class APIKey {

	public String key;
	public String value;
	public String authIn;

	public APIKey(String key, String value, String authIn) {
		this.key = key;
		this.value = value;
		this.authIn = authIn;
	}

	public HashMap<String, String> toHeader(HashMap<String, String> headers) {
		String keys = key == null ? "api_key" : key;
		headers.put(keys, value);
		return headers;
	}

	public HashMap<String, String> toQuery(HashMap<String, String> querys) {
		String keys = key == null ? "api_key" : key;
		querys.put(keys, value);
		return querys;
	}
}