package com.apiintegration.core.response;

public class DataResponse implements IDataResponse {

	public String message;
	public String token;
	public Object data;
	public String path;
	public Integer status;
	public Long timestamp;

	public DataResponse(Object data, String message, String path, int status) {
		this.path = (path == null) ? null : path;
		this.message = (message == null) ? "OK" : message;
		this.status = (status == 200 || status == 0) ? 200 : status;
		this.data = data;
		this.timestamp = System.currentTimeMillis();
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

	@Override
	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public Object getData() {
		return data;
	}

}