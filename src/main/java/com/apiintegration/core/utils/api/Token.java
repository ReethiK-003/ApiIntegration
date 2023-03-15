package com.apiintegration.core.utils.api;

import java.util.HashMap;

public class Token {

	private String token;
	private String key;

	public Token(String key, String token) {
		this.key = key;
		this.token = token;
	}

	public HashMap<String, String> toHeader(HashMap<String, String> header) {
		String _key = this.key != null ? this.key : "Authorization";
		header.put(_key, token);
		return header;
	}
}
