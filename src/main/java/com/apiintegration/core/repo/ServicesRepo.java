package com.apiintegration.core.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apiintegration.core.model.Project;
import com.apiintegration.core.model.Services;

public interface ServicesRepo extends JpaRepository<Services, Long> {

	Optional<Services> findById(Long serviesId);

//	Optional<Services> findByservicesName(String servicesName);
//	
//	Services findByIdAndServicesName(Long servicesId , String servicesName);
//	
	List<Services> findAllByProject(Project project);

//	Optional<Services> findByServiceNameAndProject(String serviceName, Project project);
	boolean existsByServiceNameAndProject(String serviceName, Project project);
//	Services findAllByProjectIdAndSeviceNameAndId(Long projectId, String servicesName, Long serviceId);
}
