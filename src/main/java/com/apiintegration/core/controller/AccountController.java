package com.apiintegration.core.controller;

import java.util.List;

import javax.mail.MethodNotSupportedException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiintegration.core.model.Account;
import com.apiintegration.core.model.Project;
import com.apiintegration.core.model.User;
import com.apiintegration.core.request.CreateAccountRequest;
import com.apiintegration.core.request.InviteUserRequest;
import com.apiintegration.core.request.UpdateAccountRequest;
import com.apiintegration.core.request.UpdateUserRequest;
import com.apiintegration.core.response.BasicResponse;
import com.apiintegration.core.response.DataResponse;
import com.apiintegration.core.response.IResponse;
import com.apiintegration.core.service.AccountService;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@PostMapping("/create")
	public IResponse createNewAccount(@Valid @RequestBody CreateAccountRequest request, @RequestAttribute User user,
			HttpServletRequest servletRequest) {

		Account account;
		try {
			account = accountService.createNewAccount(request, user);
			return new DataResponse(account, "Succesfully Created Account !!", getRequestPath(servletRequest), 200);
		} catch (MethodNotSupportedException e) {

			return new BasicResponse(e.getLocalizedMessage(), getRequestPath(servletRequest), 400);
		} catch (NotFoundException e) {

			return new BasicResponse(e.getLocalizedMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@PutMapping("/update")
	public IResponse updateAccount(@Valid @RequestBody UpdateAccountRequest request, @RequestAttribute User user,
			HttpServletRequest servletRequest) {

		try {
			Account account = accountService.updateAccount(request);
			return new DataResponse(account, "Succesfully Updated Account !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@PutMapping("/delete")
	public IResponse deleteAccount(@RequestAttribute User user, HttpServletRequest servletRequest) {

		try {
			accountService.deleteAccountRequest(user.getAccount());
			return new BasicResponse("Success !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@DeleteMapping("/confirm-delete/{token}")
	public IResponse confirmDeleteAccount(@PathVariable(name = "token") String token,
			HttpServletRequest servletRequest) {

		try {
			accountService.deleteAccountAndSanitize(token);
			return new BasicResponse("Success !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@PostMapping("/invite-member")
	public IResponse inviteUserToAccount(@Valid @RequestBody InviteUserRequest request, @RequestAttribute User user,
			HttpServletRequest servletRequest) {

		try {
			accountService.inviteNewUser(request, user);
			return new BasicResponse("Succesfully Invited User to Account !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@GetMapping("/join/{token}")
	public IResponse joinAccount(@PathVariable(name = "token") String request, @RequestAttribute User user,
			HttpServletRequest servletRequest) {

		try {
			Account account = accountService.validateAndJoinUser(request, user);
			return new DataResponse(account, "Succesfully Created Account !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@PutMapping("/update-member")
	public IResponse editUser(@Valid @RequestBody UpdateUserRequest request, @RequestAttribute User user,
			HttpServletRequest servletRequest) {

		try {
			User updatedMember = accountService.updateUser(request, user);
			return new DataResponse(updatedMember, "Succesfully Updated Member !!", getRequestPath(servletRequest),
					200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@DeleteMapping("/remove-member/{id}")
	public IResponse removeUser(@PathVariable(name = "id") Long userId, HttpServletRequest servletRequest) {

		try {
			Account account = accountService.removeUser(userId);
			return new DataResponse(account, "Removed User from account !! ", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@GetMapping("/list-member")
	public IResponse getAllAccountUsers(@RequestAttribute User user, HttpServletRequest servletRequest) {

		try {
			List<User> users = accountService.getAllUsers(user.getAccount());
			return new DataResponse(users, "Successfully fetched all Users !!", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	@GetMapping("/list-project")
	public IResponse getAllAccountProjects(@RequestAttribute User user, HttpServletRequest servletRequest) {

		try {
			List<Project> projects = accountService.getAllProjects(user.getAccount());
			return new DataResponse(projects, "Succesfully fetched all projects", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(e.getMessage(), getRequestPath(servletRequest), 400);
		}
	}

	private String getRequestPath(HttpServletRequest request) {
		return request.getRequestURL().toString();
	}
}