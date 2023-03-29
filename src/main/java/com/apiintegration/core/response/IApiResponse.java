package com.apiintegration.core.response;

import com.apiintegration.core.utils.APIDataObject;
import com.apiintegration.core.utils.ApiResponseObject;

public interface IApiResponse extends IResponse {

	public ApiResponseObject getResponseObject();

	public void setResponseObject(ApiResponseObject responseObject);

	public APIDataObject getRequestObject();

	public void setRequestObject(APIDataObject requestObject);

}