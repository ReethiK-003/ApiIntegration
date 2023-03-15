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

import com.apiintegration.core.exception.NoDataFoundException;
import com.apiintegration.core.model.Account;
import com.apiintegration.core.model.Project;
import com.apiintegration.core.model.Token;
import com.apiintegration.core.model.User;
import com.apiintegration.core.request.AddProjectToUserRequest;
import com.apiintegration.core.request.CreateAccountRequest;
import com.apiintegration.core.request.InviteUserRequest;
import com.apiintegration.core.response.BasicResponse;
import com.apiintegration.core.response.DataResponse;
import com.apiintegration.core.response.IDataResponse;
import com.apiintegration.core.response.IResponse;
import com.apiintegration.core.service.AccountService;
import com.apiintegration.core.service.ProjectService;
import com.apiintegration.core.service.TokenService;
import com.apiintegration.core.service.UserService;
import com.apiintegration.core.utils.TokenTypes;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;
	private final ProjectService projectService;

	@PostMapping("/create")
//	@PreAuthorize("hasRole('USER')") not working with preAuthorise role
	public IDataResponse createNewAccount(@Valid @RequestBody CreateAccountRequest request,
			@RequestAttribute User user) {
		log.info("Creating Account for User .. ", user);
		if (user.getAccount() == null) {
			User savedUser = accountService.createNewAccount(request, user);
			if (savedUser.getAccount() != null) {
				return new DataResponse(savedUser, "Succesfully Created Account !!", "/account/create", 200);
			}
		}
		return new DataResponse(user, "Failed to Create Account !!", "/account/create", 400);
	}

	@PostMapping("/invite")
	@PreAuthorize("hasAnyRole('LEAD', 'OWNER')")
	public IResponse inviteUserToAccount(@Valid @RequestBody InviteUserRequest request, @RequestAttribute User user) {

		try {
			accountService.inviteNewUser(request, user);
			return new BasicResponse("Succesfully Invited User to Account !!", "/account/invite", 200);
		} catch (Exception e) {
			return new BasicResponse("Failed to Invite User to Account Try Again !", "/accoun/invite", 400);
		}

	}

	@GetMapping("/join/{token}")
	@PreAuthorize("hasRole('USER')")
	public IResponse joinAccount(@PathVariable(name = "token") String request, @RequestAttribute User user) {

		try {
			accountService.validateAndJoinUser(request, user);
			return new BasicResponse("Succesfully Created Account !!", "/account/invite", 200);
		} catch (Exception e) {
			return new BasicResponse("Failed to Join !!", "/accoun/invite", 400);
		}

		
	}

	@PostMapping("/addproject")
	@PreAuthorize("hasAnyRole('LEAD', 'OWNER')")
	public IResponse addProjectToUser(@Valid @RequestBody AddProjectToUserRequest request,
			@RequestAttribute User user) {

		User savedUser = accountService.addProjectToUser(request, user);
		if (savedUser != null) {
			return new BasicResponse("Succesfully Added Project to User !!", "/account/addproject", 200);
		}
		return new BasicResponse("Failed to add Project to User !!", "/account/addproject", 400);
	}

	@GetMapping("/getprojects")
	public IDataResponse getAllAccountProjects(@RequestAttribute User user) {

		List<Project> projects = projectService.getAllProjectsByAccount(user.getAccount());
		if (projects != null) {
			return new DataResponse(projects, null, "/account/getProjects", 200);
		}
		throw new NoDataFoundException("No Projects Available !!");
	}

	@GetMapping("/getUsers")
	public IDataResponse getAllAccountUsers(@RequestAttribute User user) {

		throw new NoDataFoundException("No Projects Available !!");
	}

}
