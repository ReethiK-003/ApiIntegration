package com.apiintegration.core.service;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.apiintegration.core.model.Account;
import com.apiintegration.core.model.Project;
import com.apiintegration.core.model.RelUserProject;
import com.apiintegration.core.model.User;
import com.apiintegration.core.repo.ProjectRepo;
import com.apiintegration.core.request.CreateProjectRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepo projectRepo;
	private final UserService userService;

	@Transactional
	public Project createNewProject(CreateProjectRequest request, User user) {

		boolean checkProjectExists = projectRepo.findByProjectNameAndAccountId(request.getProjectName(),
				user.getAccount().getId()) == null;
		if (checkProjectExists) {
			Project project = new Project();

			project.setAccount(user.getAccount());
			project.setProjectName(request.getProjectName());
			project.setProjectDescription(request.getProjectDescription());

			return save(project);
		}
		throw new RuntimeException("Failed to create Project !!");
	}

	@Transactional
	public User addProjectToUser(Project project, User user) {

		RelUserProject relUserProject = new RelUserProject();
		relUserProject.setProject(project);
		relUserProject.setUser(user);
		relUserProject.setCreatedUser(user);

		user.addProject(relUserProject);

		return userService.save(user);
	}

	public List<Project> getAllProjectsForUser(User user) {
		return userService.listAllUserProjects(user);
	}

	public List<Project> getAllProjectsByAccount(Account account) {
		return projectRepo.findByAccount(account);
	}

	public Project getProjectbyId(Long projectId) {
		return projectRepo.findById(projectId).orElse(null);
	}

	public Project save(Project project) {
		return projectRepo.save(project);
	}
}