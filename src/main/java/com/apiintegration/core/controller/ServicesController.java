package com.apiintegration.core.controller;

import java.util.List;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiintegration.core.exception.NoDataFoundException;
import com.apiintegration.core.model.Project;
import com.apiintegration.core.model.Services;
import com.apiintegration.core.model.User;
import com.apiintegration.core.request.CreateServicesRequest;
import com.apiintegration.core.request.UpdateServicesRequest;
import com.apiintegration.core.response.BasicResponse;
import com.apiintegration.core.response.DataResponse;
import com.apiintegration.core.response.IDataResponse;
import com.apiintegration.core.response.IResponse;
import com.apiintegration.core.service.ServicesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServicesController {

	private final ServicesService servicesService;

	@PostMapping("/create")
	public IResponse createProject(@Valid @RequestBody CreateServicesRequest request, @RequestAttribute User user) {

		Services services = servicesService.createNewService(request);

		if (services.getId() != null) {
			return new DataResponse(services,"Service created Successfully !!", "/service/create", 200);
		}
		return new BasicResponse("Failed to create Service !!", "/service/create", 400);
	}

	@PostMapping("/edit")
	public IDataResponse updateService(@Valid @RequestBody UpdateServicesRequest request) {

		Services service = servicesService.updateService(request);
		if (service != null) {
			return new DataResponse(service ,"Service Modified Successfully !!", "/service/edit", 200);
		}
		throw new NoDataFoundException("Failed to Modify Service !!");
	}

	@GetMapping("/get/{id}")
	public IDataResponse getServices(@PathVariable(name = "id") Long servicesId) {

		Services service = servicesService.getServices(servicesId);
		if (service != null) {
			return new DataResponse(service ,"Success", "/service/get", 200);
		}
		throw new NoDataFoundException("Service Not Found !!");
	}
	
	@GetMapping("list/{id}")
	public IDataResponse getAllServicesInProject(@PathVariable(name = "id") Long projectId) {
		List<Services> services = servicesService.getAllServicesByProjectId(projectId);
		if(services != null) {
			return new DataResponse(services, "Success", "/service/list", 200);
		}
		throw new NoDataFoundException("No Services AVailable !!");
	}
}
