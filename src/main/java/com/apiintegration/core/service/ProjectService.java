package com.apiintegration.core.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiintegration.core.exception.DuplicateEntryException;
import com.apiintegration.core.exception.UserNotFoundException;
import com.apiintegration.core.model.Account;
import com.apiintegration.core.model.Project;
import com.apiintegration.core.model.RelUserProject;
import com.apiintegration.core.model.User;
import com.apiintegration.core.repo.ProjectRepo;
import com.apiintegration.core.repo.RelUserProjectRepo;
import com.apiintegration.core.request.CreateProjectRequest;
import com.apiintegration.core.request.UpdateProjectRequest;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepo projectRepo;
	private final UserService userService;
	private final ServicesService servicesService;
	private final RelUserProjectRepo relUserProjectRepo;

	@Transactional
	public Project createNewProject(CreateProjectRequest request, User user)
			throws DuplicateEntryException, UserNotFoundException {

		if (!projectRepo.existsByProjectNameAndAccount(request.getProjectName(), user.getAccount())) {
			Project project = new Project();

			project.setAccount(user.getAccount());
			project.setProjectName(request.getProjectName());
			project.setProjectDescription(request.getProjectDescription());
			save(project);

			addProjectToUser(project, user.getAccount().getUser());
			return project;
		} else {
			throw new DuplicateEntryException("Project with name already exists in Account !!");
		}
	}

	@Transactional
	public Project updateProject(UpdateProjectRequest request) throws NotFoundException {

		Project project = getProject(request.getProjectId());

		Optional.ofNullable(request.getProjectName()).ifPresent(project::setProjectName);
		Optional.ofNullable(request.getProjectDescription()).ifPresent(project::setProjectDescription);

		return save(project);
	}

	public void deleteProjectAndSanitize(Project project) {
		// Update a Cascade type on model to automatically perform delete or detach
		// operation.
		servicesService.deleteAllServicesByProject(project);

		relUserProjectRepo.deleteAll(getAllUserProjectsByProject(project));

		projectRepo.delete(project);
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

	public void deleteAllProjectByAccount(Account account) {
		List<Project> projectList = getAllProjectsByAccount(account);
		for (Project project : projectList) {
			deleteProjectAndSanitize(project);
		}
	}

	public List<Project> getAllProjectsByAccount(Account account) {
		return projectRepo.findByAccount(account);
	}

	public Project getProject(Long projectId) throws NotFoundException {
		return projectRepo.findById(projectId).orElseThrow(() -> new NotFoundException("Project with id not found !!"));
	}

	private List<RelUserProject> getAllUserProjectsByProject(Project project) {
		return relUserProjectRepo.findAllByProject(project);
	}

	public Project save(Project project) {
		return projectRepo.save(project);
	}
}
