package com.apiintegration.core.utils.api;

import java.util.HashMap;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Auth {

	private String authType;

	private String authIn;

	private String key;

	private String value;

	private String username;

	private String password;

	private String token;

}
