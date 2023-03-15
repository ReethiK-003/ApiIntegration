package com.apiintegration.core.response;

import com.apiintegration.core.utils.APIDataObject;
import com.apiintegration.core.utils.ApiResponseObject;

public class ApiResponse implements IApiResponse {

	public String message;
	public String token;
	public String path;
	public ApiResponseObject responseObject;
	public APIDataObject requestObject;
	public Integer status;
	public Long timestamp;

	public ApiResponse(APIDataObject request, ApiResponseObject response, String path, int status) {
		this.path = (path == null) ? null : path;
		this.message = (status == 200) ?"Success !!" : "Failed !!";
		this.status = (status == 200 || status == 0) ? 200 : status;
		this.requestObject = request;
		this.responseObject = response;
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
	public ApiResponseObject getResponseObject() {
		return responseObject;
	}

	@Override
	public void setResponseObject(ApiResponseObject responseObject) {
		this.responseObject = responseObject;
	}

	@Override
	public APIDataObject getRequestObject() {
		return requestObject;
	}

	@Override
	public void setRequestObject(APIDataObject requestObject) {
		this.requestObject = requestObject;
	}
}
