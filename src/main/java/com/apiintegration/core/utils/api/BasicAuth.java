package com.apiintegration.core.utils.api;

import java.util.HashMap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BasicAuth{

	public String userName;
	public String password;
	public String authIn;

	public BasicAuth(String userName, String password) {
		this.userName = userName;
		this.password = password;
		this.authIn = AuthIn.HEADER;
	}

	public HashMap<String, String> toHeader(HashMap<String, String> headers) {
		String value = "";
		headers.put("Authorization", value);
		return headers;
	}
}
