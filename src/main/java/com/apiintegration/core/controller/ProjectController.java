package com.apiintegration.core.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiintegration.core.model.Project;
import com.apiintegration.core.model.User;
import com.apiintegration.core.request.CreateProjectRequest;
import com.apiintegration.core.request.UpdateProjectRequest;
import com.apiintegration.core.response.BasicResponse;
import com.apiintegration.core.response.DataResponse;
import com.apiintegration.core.response.IResponse;
import com.apiintegration.core.service.ProjectService;
import com.apiintegration.core.utils.UserRole;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

	private final ProjectService projectService;

	@PostMapping("/create")
	public IResponse createProject(@Valid @RequestBody CreateProjectRequest request, @RequestAttribute User user,
			HttpServletRequest servletRequest) {

		try {
			Project project = projectService.createNewProject(request, user);
			if (!user.getUserRole().equals(UserRole.OWNER)) {
				projectService.addProjectToUser(project, user);
			}
			return new DataResponse(project, "Project created Successfully !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@PutMapping("/update")
	public IResponse updateProject(@Valid @RequestBody UpdateProjectRequest request, @RequestAttribute User user,
			HttpServletRequest servletRequest) {

		try {
			Project project = projectService.updateProject(request);

			return new DataResponse(project, "Project updated Successfully !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@GetMapping("/get/{id}")
	public IResponse getProject(@PathVariable(name = "id") Long projectId, @RequestAttribute User user,
			HttpServletRequest servletRequest) {
		try {
			Project project = projectService.getProject(projectId);

			if (user.getProjects().stream().anyMatch(p -> p.getId().equals(project.getId()))) {
				return new DataResponse(project, "Success !!", getRequestPath(servletRequest), 200);
			}
			return new BasicResponse("No access to view this project.", getRequestPath(servletRequest),
					HttpStatus.UNAUTHORIZED.value());
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@GetMapping("/list")
	public IResponse listAllProjects(@RequestAttribute User user, HttpServletRequest servletRequest) {

		try {
			List<Project> projects = user.getProjects();
			return new DataResponse(projects, "Success !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}

	}

	@DeleteMapping("/delete/{id}")
	public IResponse deleteProject(@PathVariable(name = "id") Long projectId, HttpServletRequest servletRequest) {
		try {
			Project project = projectService.getProject(projectId);
			projectService.deleteProjectAndSanitize(project);
			return new BasicResponse("Successfully Deleted project !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	private String getRequestPath(HttpServletRequest request) {
		return request.getRequestURL().toString();
	}
}
