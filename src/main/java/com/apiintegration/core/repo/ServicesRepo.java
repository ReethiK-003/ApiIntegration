package com.apiintegration.core.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apiintegration.core.model.Services;

public interface ServicesRepo extends JpaRepository<Services, Long>{

	Optional<Services> findById(Long serviesId);
	
//	Optional<Services> findByservicesName(String servicesName);
//	
//	Services findByIdAndServicesName(Long servicesId , String servicesName);
//	
	List<Services> findAllByProjectId(Long projectId);
//	
	Services findByServiceNameAndProjectId(String serviceName, Long projectId);
//	
//	Services findAllByProjectIdAndSeviceNameAndId(Long projectId, String servicesName, Long serviceId);
}
