package com.apiintegration.core.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiintegration.core.model.Api;
import com.apiintegration.core.request.CreateApiRequest;
import com.apiintegration.core.request.TestApiRequest;
import com.apiintegration.core.request.UpdateApiRequest;
import com.apiintegration.core.response.ApiResponse;
import com.apiintegration.core.response.BasicResponse;
import com.apiintegration.core.response.DataResponse;
import com.apiintegration.core.response.IResponse;
import com.apiintegration.core.service.ApiService;
import com.apiintegration.core.utils.APIDataObject;
import com.apiintegration.core.utils.ApiResponseObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

	private final ApiService apiService;

	@PostMapping("/create")
	public IResponse createApi(@Valid @RequestBody CreateApiRequest request, HttpServletRequest servletRequest) {

		try {
			Api newApi = apiService.createNewApi(request);

			if (newApi.getId() != null) {
				return new DataResponse(newApi, "API created successfully !!",
						servletRequest.getRequestURL().toString(), 200);
			}
			return new BasicResponse("Failed 1 !!", "/api/create", 400);
		} catch (Exception e) {
			log.error("Failed to create API ,", e);
			return new BasicResponse("Failed !!", "/api/create", 400);
		}
	}

	@GetMapping("/get/{id}")
	public IResponse getApibyId(@PathVariable Long id, HttpServletRequest servletRequest) {

		Api api = apiService.getApibyId(id);// Generate new Response type with DataObject and set the data.
		if (api != null) {
			return new DataResponse(api, "API created successfully !!", servletRequest.getRequestURL().toString(), 200);
		}
		return new BasicResponse("Api Not Found !!", servletRequest.getRequestURL().toString(), 400);
	}

	@GetMapping("/list/{id}")
	public IResponse getAllApi(@PathVariable Long serviceId, HttpServletRequest servletRequest) {
		List<Api> apis = apiService.getAllApis(serviceId);
		if (apis != null) {
			return new DataResponse(apis, "Success !!", servletRequest.getRequestURL().toString(), 200);
		}
		return new BasicResponse("Failed !!", servletRequest.getRequestURL().toString(), 400);
	}

	@PutMapping("/edit")
	public IResponse editApi(@RequestBody UpdateApiRequest request, HttpServletRequest servletRequest) {
		Api api = apiService.modifyApi(request);
		if (api != null) {
			return new DataResponse(api, "API Modified successfully !!", servletRequest.getRequestURL().toString(),
					200);
		}
		return new BasicResponse("Api Not Found !!", servletRequest.getRequestURL().toString(), 400);

	}

	@DeleteMapping("/delete/{id}")
	public IResponse deleteApi() {

		// implement method
		return null;
	}

	@PostMapping("/test")
	public IResponse testApi(@Valid @RequestBody TestApiRequest request) {

		try {
			APIDataObject requestObject = request.getData();

			ApiResponseObject responseObject = apiService.processAndFetchApiResponse(request);

			// need to create methods for saving API response in logs.

			return new ApiResponse(requestObject, responseObject, "/api/test", 200);
		} catch (Exception e) {
			return new BasicResponse("Failed to validate !!", "/api/test", 400);
		}
	}
}