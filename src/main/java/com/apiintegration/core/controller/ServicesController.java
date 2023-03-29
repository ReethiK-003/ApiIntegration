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
import com.apiintegration.core.model.Project;
import com.apiintegration.core.model.Services;
import com.apiintegration.core.model.User;
import com.apiintegration.core.request.CreateServicesRequest;
import com.apiintegration.core.request.UpdateServicesRequest;
import com.apiintegration.core.response.BasicResponse;
import com.apiintegration.core.response.DataResponse;
import com.apiintegration.core.response.IResponse;
import com.apiintegration.core.service.ProjectService;
import com.apiintegration.core.service.ServicesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServicesController {

	private final ServicesService servicesService;
	private final ProjectService projectService;

	@PostMapping("/create")
	public IResponse createService(@Valid @RequestBody CreateServicesRequest request, @RequestAttribute User user,
			HttpServletRequest servletRequest) {

		try {
			Project project = projectService.getProject(request.getProjectId());
			Services services = servicesService.createNewService(request, project);

			return new DataResponse(services, "Service created Successfully !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			e.printStackTrace();
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@PutMapping("/update")
	public IResponse updateService(@Valid @RequestBody UpdateServicesRequest request,
			HttpServletRequest servletRequest) {

		try {
			Services service = servicesService.updateService(request);

			return new DataResponse(service, "Service Modified Successfully !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@GetMapping("/get/{id}")
	public IResponse getService(@PathVariable(name = "id") Long servicesId, HttpServletRequest servletRequest) {
		try {
			Services service = servicesService.getServices(servicesId);

			return new DataResponse(service, "Service Modified Successfully !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@GetMapping("list/{id}")
	public IResponse listServiceByProject(@PathVariable(name = "id") Long projectId,
			HttpServletRequest servletRequest) {
		try {
			Project project = projectService.getProject(projectId);
			List<Services> services = servicesService.getServicesByProject(project);
			return new DataResponse(services, "Success", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@DeleteMapping("/delete/{id}")
	public IResponse deleteService(@PathVariable(name = "id") Long serviceId, HttpServletRequest servletRequest) {
		try {
			Services services = servicesService.getServices(serviceId);
			servicesService.deleteServiceAndSanitize(services);
			return new BasicResponse("Success", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	private String getRequestPath(HttpServletRequest request) {
		return request.getRequestURL().toString();
	}
}