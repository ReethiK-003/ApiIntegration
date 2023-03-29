package com.apiintegration.core.utils;

public class UserRole {

	public static final String USER = "USER";
	public static final String OWNER = "OWNER";
	public static final String LEAD = "LEAD";
	public static final String SUPERDEV = "SUPERDEV";
	public static final String DEV = "DEV";

	public static boolean isValidRole(String role) {
		role = role.toUpperCase().strip();

		switch (role) {
		case "LEAD":
		case "SUPERDEV":
		case "DEV":
			return true;

		default:
			return false;
		}
	}
}