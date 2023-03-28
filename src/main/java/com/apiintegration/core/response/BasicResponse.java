package com.apiintegration.core.response;

public class BasicResponse implements IResponse {

	public String message;
	public String token;
	public String path;
	public Integer status;
	public Long timestamp;
	private Throwable error;

	public BasicResponse(String message, String path, int status) {
		this.path = (path == null) ? null : path;
		this.message = (message == null) ? "OK" : message;
		this.status = (status == 200 || status == 0) ? 200 : status;
		this.timestamp = System.currentTimeMillis();
	}

	public BasicResponse(String message, Throwable cause) {
		this.message = message;
		this.error = cause;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getToken() {
		return token;
	}

	@Override
	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public Integer getStatus() {
		return status;
	}

	@Override
	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public Long getTimestamp() {
		return timestamp;
	}

	@Override
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Throwable getError() {
		return error;
	}

	public void setError(Throwable error) {
		this.error = error;
	}

}