package com.apiintegration.core.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiintegration.core.model.Api;
import com.apiintegration.core.model.Services;
import com.apiintegration.core.request.CreateApiRequest;
import com.apiintegration.core.request.TestApiRequest;
import com.apiintegration.core.request.UpdateApiRequest;
import com.apiintegration.core.request.UpdateServicesRequest;
import com.apiintegration.core.response.ApiResponse;
import com.apiintegration.core.response.BasicResponse;
import com.apiintegration.core.response.DataResponse;
import com.apiintegration.core.response.IResponse;
import com.apiintegration.core.service.ApiService;
import com.apiintegration.core.service.ServicesService;
import com.apiintegration.core.utils.APIDataObject;
import com.apiintegration.core.utils.ApiResponseObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

	private final ApiService apiService;
	private final ServicesService servicesService;

	@PostMapping("/create")
	public IResponse createApi(@Valid @RequestBody CreateApiRequest request, HttpServletRequest servletRequest) {

		try {
			Services service = servicesService.getServices(request.getServiceId());
			Api api = apiService.createApi(request, service);
			return new DataResponse(api, "API created successfully !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@PutMapping("/update")
	public IResponse updateApi(@Valid @RequestBody UpdateApiRequest request, HttpServletRequest servletRequest) {

		try {
			Api api = apiService.updateApi(request);
			return new DataResponse(api, "API Modified successfully !!", servletRequest.getRequestURL().toString(),
					200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@GetMapping("/get/{id}")
	public IResponse getApi(@PathVariable(name = "id") Long apiId, HttpServletRequest servletRequest) {

		try {
			Api api = apiService.getApi(apiId);// generate new Response type with DataObject and set the data ,for now
												// just
												// using simple response.
			return new DataResponse(api, "Success !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@GetMapping("/list/{id}")
	public IResponse getAllApi(@PathVariable(name = "id") Long serviceId, HttpServletRequest servletRequest) {
		try {
			Services services = servicesService.getServices(serviceId);
			List<Api> apis = apiService.getApisByServices(services);

			return new DataResponse(apis, "Success !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse("Failed !!", getRequestPath(servletRequest), 400);
		}
	}

	@DeleteMapping("/delete/{id}")
	public IResponse deleteApi(@PathVariable(name = "id") Long apiId, HttpServletRequest servletRequest) {

		try {
			apiService.deleteApi(apiId);
			return new BasicResponse("Success !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@PostMapping("/test")
	public IResponse testApi(@Valid @RequestBody TestApiRequest request) {

		try {
			APIDataObject requestObject = request.getData();

			ApiResponseObject responseObject = apiService.processAndFetchApiResponse(request);

			// need to create methods for saving API response in logs.

			return new ApiResponse(requestObject, responseObject, "/api/test", 200);
		} catch (Exception e) {
			e.printStackTrace();
			return new BasicResponse("Failed to validate !!", "/api/test", 400);
		}
	}

	private String getRequestPath(HttpServletRequest request) {
		return request.getRequestURL().toString();
	}
}
