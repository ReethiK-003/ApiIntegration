package com.apiintegration.core.service;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiintegration.core.exception.DuplicateEntryException;
import com.apiintegration.core.exception.EntryNotFoundException;
import com.apiintegration.core.model.Project;
import com.apiintegration.core.model.Services;
import com.apiintegration.core.repo.ServicesRepo;
import com.apiintegration.core.request.CreateServicesRequest;
import com.apiintegration.core.request.UpdateServicesRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
public class ServicesService {

	private final ServicesRepo servicesRepo;
	private final ProjectService projectService;

	public Services createNewService(CreateServicesRequest request) {

		Project project = projectService.getProject(request.getProjectId());
		boolean checkServiceExists = servicesRepo.findByServiceNameAndProjectId(request.getServiceName(),
				project.getId()) == null;
		if (checkServiceExists) {
			Services service = new Services();

			service.setProject(project);
			service.setServiceName(request.getServiceName());
			service.setServiceBaseUrl(request.getBaseUrl().toString());
			service.setServiceBaseUrlLive(request.getBaseUrlLive().toString());
			service.setEnvLive(request.isLive());

			return save(service);
		}
		throw new DuplicateEntryException("Service with name already created try with new name");
	}

	public Services updateService(UpdateServicesRequest request) {

		Services service = getServices(request.getServiceId());
		if (service != null) {
			String baseUrl = request.getBaseUrl() == null ? service.getServiceBaseUrl()
					: request.getBaseUrl().toString();
			String baseUrlLive = request.getBaseUrlLive() == null ? service.getServiceBaseUrlLive()
					: request.getBaseUrlLive().toString();

			service.setServiceBaseUrl(baseUrl);
			service.setServiceBaseUrlLive(baseUrlLive);

			return save(service);
		}
		throw new DuplicateEntryException("Service not found !!");
	}

	public List<Services> getAllServicesByProjectId(Long projectId) {
		Project project = projectService.getProject(projectId);
		if (project != null) {
			return getServicesByProject(project);
		}
		throw new EntryNotFoundException("Project not found please try again !");
	}

	public Services save(Services services) {
		return servicesRepo.save(services);
	}

	public Services getServices(Long id) {
		return servicesRepo.findById(id).orElse(null);
	}

	public List<Services> getServicesByProject(Project project) {
		return servicesRepo.findAllByProjectId(project.getId());
	}
}
