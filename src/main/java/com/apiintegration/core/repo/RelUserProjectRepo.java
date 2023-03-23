package com.apiintegration.core.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.apiintegration.core.model.RelUserProject;

public interface RelUserProjectRepo extends JpaRepository<RelUserProject, Long> {

	Optional<RelUserProject> findAllByUserId(Long userId);

}