package com.apiintegration.core.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiintegration.core.exception.DuplicateEntryException;
import com.apiintegration.core.model.Project;
import com.apiintegration.core.model.Services;
import com.apiintegration.core.repo.ServicesRepo;
import com.apiintegration.core.request.CreateServicesRequest;
import com.apiintegration.core.request.UpdateServicesRequest;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicesService {

	private final ServicesRepo servicesRepo;
	private final ApiService apiService;

	@Transactional
	public Services createNewService(CreateServicesRequest request, Project project)
			throws NotFoundException, DuplicateEntryException {

		if (!servicesRepo.existsByServiceNameAndProject(request.getServiceName(), project)) {
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

	public Services updateService(UpdateServicesRequest request) throws NotFoundException {

		Services service = getServices(request.getServiceId());
		if (service != null) {
			String baseUrl = request.getBaseUrl() == null ? service.getServiceBaseUrl()
					: request.getBaseUrl().toString();
			String baseUrlLive = request.getBaseUrlLive() == null ? service.getServiceBaseUrlLive()
					: request.getBaseUrlLive().toString();

			Optional.ofNullable(request.getServiceName()).ifPresent(service::setServiceName);
			service.setServiceBaseUrl(baseUrl);
			service.setServiceBaseUrlLive(baseUrlLive);
			service.setEnvLive(request.isLive());

			return save(service);
		}
		throw new DuplicateEntryException("Service not found !!");
	}

	public void deleteServiceAndSanitize(Services services) {
		apiService.deleteAllApiByService(services);
		servicesRepo.delete(services);
	}

	public void deleteAllServicesByProject(Project project) {
		List<Services> servicesList = getServicesByProject(project);
		for (Services services : servicesList) {
			deleteServiceAndSanitize(services);
		}
	}

	public Services getServices(Long id) throws NotFoundException {
		return servicesRepo.findById(id).orElseThrow(() -> new NotFoundException("Service not found for id !!"));
	}

	public List<Services> getServicesByProject(Project project) {
		return servicesRepo.findAllByProject(project);
	}

	public Services save(Services services) {
		return servicesRepo.save(services);
	}
}