package com.apiintegration.core.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apiintegration.core.model.Services;

public interface ServicesRepo extends JpaRepository<Services, Long> {

	Optional<Services> findById(Long serviesId);

	List<Services> findAllByProjectId(Long projectId);

	Services findByServiceNameAndProjectId(String serviceName, Long projectId);
}