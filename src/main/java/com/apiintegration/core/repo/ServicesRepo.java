package com.apiintegration.core.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apiintegration.core.model.Project;
import com.apiintegration.core.model.Services;

public interface ServicesRepo extends JpaRepository<Services, Long> {

	Optional<Services> findById(Long serviesId);

	List<Services> findAllByProject(Project project);

	boolean existsByServiceNameAndProject(String serviceName, Project project);

}