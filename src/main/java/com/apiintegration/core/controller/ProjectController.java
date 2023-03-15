package com.apiintegration.core.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiintegration.core.model.Project;
import com.apiintegration.core.model.User;
import com.apiintegration.core.request.CreateProjectRequest;
import com.apiintegration.core.response.BasicResponse;
import com.apiintegration.core.response.DataResponse;
import com.apiintegration.core.response.IResponse;
import com.apiintegration.core.service.ProjectService;
import com.apiintegration.core.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

	private final ProjectService projectService;

	@PostMapping("/create")
//	@PreAuthorize("hasAnyRole('LEAD', 'OWNER' , 'SUPERDEV')")
	public IResponse createProject(@Valid @RequestBody CreateProjectRequest request, @RequestAttribute User user) {

		Project project = projectService.createNewProject(request, user);

		if (project.getId() != null) {
			user = projectService.addProjectToUser(project, user);
			return new DataResponse(user ,"Project created Successfully !!", "/project/create", 200);
		}
		return new BasicResponse("Failed to create project !!", "/project/create", 400);
	}

	@GetMapping("/get/{id}")
	public IResponse getProject(@PathVariable(name = "id") Long projectId) {
		Project project = projectService.getProject(projectId);
		if (project != null) {
			return new DataResponse(project,"Success !!", "/project/get", 200);
		}
		return new BasicResponse("Failed !!", "/project/get", 404);
	}
	
	@GetMapping("/list")
	public IResponse ListUserProjects(@RequestAttribute User user) {
		List<Project> projects = projectService.getAllProjectsForUser(user);
		if (projects != null) {
			return new DataResponse(projects,"Success !!", "/project/get", 200);
		}
		return new BasicResponse("Failed !!", "/project/get", 404);
	}
}
