package com.apiintegration.core.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apiintegration.core.model.Account;
import com.apiintegration.core.model.Project;

public interface ProjectRepo extends JpaRepository<Project, Long> {

	Optional<Project> findById(Long projectId);

	boolean existsByProjectNameAndAccount(String projectName, Account account);

	List<Project> findByAccount(Account account);

}
