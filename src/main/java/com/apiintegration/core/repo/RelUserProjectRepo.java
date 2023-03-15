package com.apiintegration.core.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apiintegration.core.model.RelUserProject;
import com.apiintegration.core.model.User;

public interface RelUserProjectRepo extends JpaRepository<RelUserProject, Long> {

	Optional<RelUserProject> findAllByUserId(Long userId);
//	
//	Optional<RelUserProject> findAllByProjectId(Long projectId);
//	
//	RelUserProject findByUserIdAndProjectId(Long userId ,Long ProjectId);
//	
//	Optional<RelUserProject> findAllByCreatedUser(User createdUser);
	
}
