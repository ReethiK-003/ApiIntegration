package com.apiintegration.core.response;

public interface IResponse {

	public String getMessage();

	public void setMessage(String message);

	public String getToken();

	public void setToken(String token);

	public String getPath();

	public void setPath(String path);

	public Integer getStatus();

	public void setStatus(Integer status);

	public Long getTimestamp();

	public void setTimestamp(Long timestamp);

}