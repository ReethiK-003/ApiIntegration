package com.apiintegration.core.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.el.MethodNotFoundException;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.apiintegration.core.exception.EntryNotFoundException;
import com.apiintegration.core.exception.NoDataFoundException;
import com.apiintegration.core.model.Api;
import com.apiintegration.core.model.Services;
import com.apiintegration.core.repo.ApiRepo;
import com.apiintegration.core.request.CreateApiRequest;
import com.apiintegration.core.request.TestApiRequest;
import com.apiintegration.core.request.UpdateApiRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.apiintegration.core.utils.APIDataObject;
import com.apiintegration.core.utils.ApiResponseObject;
import com.apiintegration.core.utils.api.APIKey;
import com.apiintegration.core.utils.api.ApiMethod;
import com.apiintegration.core.utils.api.Auth;
import com.apiintegration.core.utils.api.AuthIn;
import com.apiintegration.core.utils.api.AuthTypes;
import com.apiintegration.core.utils.api.BasicAuth;
import com.apiintegration.core.utils.api.Token;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiService {

	private final ApiRepo apiRepo;
	private final ServicesService servicesService;
	private final ObjectMapper objectMapper;

	public Api createNewApi(@Valid CreateApiRequest request) throws JsonMappingException, JsonProcessingException {

		Services service = servicesService.getServices(request.getServiceId());
		if (service != null) {
			Auth auth = request.getApiAuth();

			try {
				System.out.println(objectMapper.writeValueAsString(auth));
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			HashMap<String, String> headers = request.getApiHeader() != null ? request.getApiHeader() : new HashMap<>();
			HashMap<String, String> queryParam = request.getApiQueryParam() != null ? request.getApiQueryParam()
					: new HashMap<>();

			Api api = new Api();

			api.setServices(service);
			api.setApiName(request.getApiName());
			api.setApiMethod(request.getApiMethod());
			api.setApiPathUrl(request.getApiUrl());
			api.setApiBodyObject(request.getApiBody());
			api.setApiAuthType(auth.getAuthType());

			api.setApiAuthIn(auth.getAuthIn());

			if (auth.getAuthIn().equals(AuthIn.HEADER)) {
				headers = composeAndAddHeaders(auth, headers);
			} else if (auth.getAuthIn().equals(AuthIn.QUERY)) {
				queryParam = composeAndAddParams(auth, queryParam);
			}

			api.setApiHeaderPairs(headers);
			api.setApiQueryParam(queryParam);

			return save(api);
		}
		throw new EntryNotFoundException("Service not found to create API please try again !");
	}

	public Api modifyApi(UpdateApiRequest request) {
		Auth auth = request.getApiAuth();

		Api api = getApi(request.getId());

		HashMap<String, String> headers = request.getApiHeader() != null ? request.getApiHeader()
				: api.getApiHeaderPairs();
		HashMap<String, String> queryParam = request.getApiQueryParam() != null ? request.getApiQueryParam()
				: api.getApiQueryParam();

		Optional.ofNullable(request.getApiName()).ifPresent(api::setApiName);
		Optional.ofNullable(request.getApiMethod()).ifPresent(api::setApiMethod);
		Optional.ofNullable(request.getApiUrl()).ifPresent(api::setApiPathUrl);
		Optional.ofNullable(request.getApiBody()).ifPresent(api::setApiBodyObject);
		if (auth != null) {
			Optional.ofNullable(auth.getAuthIn()).ifPresent(api::setApiAuthIn);
			Optional.ofNullable(auth.getAuthType()).ifPresent(api::setApiAuthType);
			if (auth.getAuthIn().equals(AuthIn.HEADER)) {
				api.setApiHeaderPairs(composeAndAddHeaders(auth, headers));
			} else if (auth.getAuthIn().equals(AuthIn.QUERY)) {
				api.setApiQueryParam(composeAndAddParams(auth, queryParam));
			}
		}
		if (request.getApiHeader() != null) {
			api.setApiHeaderPairs(headers);
		}
		if (request.getApiQueryParam() != null) {
			api.setApiQueryParam(queryParam);
		}

		return save(api);

	}

	private HashMap<String, String> composeAndAddHeaders(Auth auth, HashMap<String, String> headers) {
		switch (auth.getAuthType()) {
		case AuthTypes.API_KEY:
			return new APIKey(auth.getKey(), auth.getValue(), auth.getAuthIn()).toHeader(headers);
		case AuthTypes.TOKEN:
			return new Token(auth.getKey(),auth.getToken()).toHeader(headers);
		case AuthTypes.BASIC_AUTH:
			return new BasicAuth(auth.getUsername(), auth.getPassword()).toHeader(headers);
		case AuthTypes.NO_AUTH:
			// No Auth type Don't need any headers
		default:
			return headers;
		}
	}

	private HashMap<String, String> composeAndAddParams(Auth auth, HashMap<String, String> params) {
		switch (auth.getAuthType()) {
		case AuthTypes.API_KEY:
			return new APIKey(auth.getKey(), auth.getValue(), auth.getAuthIn()).toQuery(params);
		default:
			return params;
		}
	}

	public ApiResponseObject processAndFetchApiResponse(TestApiRequest request) {

		try {
			Api api = getApi(request.getApiId());
			APIDataObject requestObject = request.getData();

			URI composedurl = composeUrl(api, requestObject);
			Object body = requestObject.getBodyObject();
			Consumer<HttpHeaders> header = composeHeaders(requestObject.getHeaderPairs());

			ResponseEntity<Object> response;
			WebClient client = WebClient.builder().build();

// create a Selection statement for API method here and create a webclient builder.
			switch (api.getApiMethod()) {
			case ApiMethod.GET:
				response = client.get().uri(composedurl).headers(header).retrieve().toEntity(Object.class).block();
				break;
			case ApiMethod.POST:
				response = client.post().uri(composedurl).bodyValue(body).headers(header).retrieve()
						.toEntity(Object.class).block();
				break;
			case ApiMethod.PUT:
				response = client.put().uri(composedurl).bodyValue(body).headers(header).retrieve()
						.toEntity(Object.class).block();
				break;
			case ApiMethod.DELETE:
				response = client.delete().uri(composedurl).headers(header).retrieve().toEntity(Object.class).block();
			default:
				throw new MethodNotFoundException("Unknown Method Type :" + api.getApiMethod());
			}
			if (response != null) {
				ApiResponseObject responseObject = new ApiResponseObject(composedurl, response.getBody(),
						response.getHeaders(), response.getStatusCodeValue());

				return responseObject;
			} else {
				throw new RuntimeException("Error Occured while Performing API test action !!");
			}
		} catch (URISyntaxException e) {
			log.error("Failed to format URI for request ");
			throw new RuntimeException("Exception occured while formatting url for API request ", e);
		}
	}

	private URI composeUrl(Api api, APIDataObject requestObject) throws URISyntaxException {

		Services service = servicesService.getServices(api.getServices().getId());

		String formattedUrl = null;
		if (service.isEnvLive()) {
			formattedUrl = service.getServiceBaseUrlLive();
		} else {
			formattedUrl = service.getServiceBaseUrl();
		}
		formattedUrl = formattedUrl + api.getApiPathUrl();
		HashMap<String, String> queryParam = requestObject.getQueryParam();
		if (queryParam != null) {
			formattedUrl = formattedUrl + "?";
			int i = 0;
			for (String key : queryParam.keySet()) {
				if (i >= 1) {
					formattedUrl = formattedUrl + "&";
				}
				formattedUrl = formattedUrl.concat(key + "=" + queryParam.get(key));
				i++;
			}
		}
		return new URI(formattedUrl.toString());
	}

	private Consumer<HttpHeaders> composeHeaders(HashMap<String, String> request) {
		return headers -> {
			request.forEach((key, value) -> headers.add(key, value));
		};
	}

	public Api getApi(Long id) {
		return apiRepo.findById(id).orElseThrow(() -> new NoDataFoundException("Api Not Found !!"));
	}

	public List<Api> getAllApis(Long serviceId) {
		return apiRepo.findByServicesId(serviceId);
	}

	private Api save(Api api) {
		return apiRepo.save(api);
	}
}
