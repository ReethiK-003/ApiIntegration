package com.apiintegration.core.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import com.apiintegration.core.request.UpdateUserRequest;
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
	public IDataResponse createNewAccount(@Valid @RequestBody CreateAccountRequest request,
			@RequestAttribute User user) {

		try {
			Account account = accountService.createNewAccount(request, user);

			return new DataResponse(account, "Succesfully Created Account !!", "/account/create", 200);
		} catch (Exception e) {
			return new DataResponse(user, e.getMessage(), "/account/create", 400);
		}
	}

	@PostMapping("/invite")
	public IResponse inviteUserToAccount(@Valid @RequestBody InviteUserRequest request, @RequestAttribute User user) {

		try {
			accountService.inviteNewUser(request, user);
			return new BasicResponse("Succesfully Invited User to Account !!", "/account/invite", 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), "/accoun/invite", 400);
		}

	}

	@GetMapping("/join/{token}")
	public IResponse joinAccount(@PathVariable(name = "token") String request, @RequestAttribute User user) {

		try {
			accountService.validateAndJoinUser(request, user);
			return new BasicResponse("Succesfully Created Account !!", "/account/invite", 200);
		} catch (Exception e) {
			return new BasicResponse("Failed to Join !!", "/accoun/invite", 400);
		}

	}

//	@PostMapping("/addproject")
//	public IResponse addProjectToUser(@Valid @RequestBody AddProjectToUserRequest request,
//			@RequestAttribute User user) {
//
//		try {
//			User savedUser = accountService.addProjectToUser(request, user);
//			return new DataResponse(savedUser, "Succesfully Added Project to User !!", "/account/addproject", 200);
//		} catch (Exception e) {
//			return new BasicResponse(e.getMessage(), "/account/addproject", 400);
//		}
//	}

	@GetMapping("/getprojects")
	public IDataResponse getAllAccountProjects(@RequestAttribute User user) {

		List<Project> projects = projectService.getAllProjectsByAccount(user.getAccount());
		if (projects != null) {
			return new DataResponse(projects, null, "/account/getProjects", 200);
		}
		throw new NoDataFoundException("No Projects Available !!");
	}

	@PutMapping("/updateUser")
	public IDataResponse editUser(@Valid @RequestBody UpdateUserRequest request, @RequestAttribute User user,
			HttpServletRequest servletRequest) {
		
//		User updatedMember = accountService.updateUser(request, user);
//		return new DataResponse(updatedMember, "Succesfully Updated User !!", "", 200);
		return null;
	}

	@GetMapping("/getUsers")
	public IDataResponse getAllAccountUsers(@RequestAttribute User user) {

		throw new NoDataFoundException("No Projects Available !!");
	}
}
