package com.apiintegration.core.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.apiintegration.core.model.Project;
import com.apiintegration.core.model.RelUserProject;

public interface RelUserProjectRepo extends JpaRepository<RelUserProject, Long> {
	
	List<RelUserProject> findAllByUserId(Long userId);

	List<RelUserProject> findAllByProject(Project project);
//	
//	Optional<RelUserProject> findAllByProjectId(Long projectId);
//	
//	RelUserProject findByUserIdAndProjectId(Long userId ,Long ProjectId);
//	
//	Optional<RelUserProject> findAllByCreatedUser(User createdUser);

}
