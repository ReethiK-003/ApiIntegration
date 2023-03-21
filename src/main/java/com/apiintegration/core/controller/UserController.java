package com.apiintegration.core.controller;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.apiintegration.core.exception.InvalidTokenException;
import com.apiintegration.core.model.User;
import com.apiintegration.core.model.UserVisits;
import com.apiintegration.core.request.LoginRequest;
import com.apiintegration.core.request.SignupRequest;
import com.apiintegration.core.request.VerifyEmailRequest;
import com.apiintegration.core.request.VerifyLoginRequest;
import com.apiintegration.core.response.BasicResponse;
import com.apiintegration.core.response.IResponse;
import com.apiintegration.core.service.TokenService;
import com.apiintegration.core.service.UserService;
import com.apiintegration.core.utils.TokenTypes;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final TokenService tokenService;

	@ApiOperation(value = "Create a new user", notes = "Create a new user with the given details")
	@ApiResponses(value = { 
	    @ApiResponse(code = 200, message = "User Created Successfully"),
	    @ApiResponse(code = 400, message = "Email already exists or Bad Request"),
	    @ApiResponse(code = 500, message = "Internal Server Error !!") })
	@PostMapping("/signup")
	public IResponse signup(@Valid @RequestBody SignupRequest signupRequest, HttpServletRequest servletRequest) {
		try {
			User newUser = userService.createNewUser(signupRequest);

			if (newUser == null) {
				return generateBasicErrorResponse("Email already exists please try with different email !!",
						servletRequest.getRequestURL().toString(), HttpStatus.CONFLICT.value());
			} else if (newUser.getId() != null) {
				userService.saveUserVisit(newUser, new UserVisits(servletRequest.getHeader("X-Forwarded-For"),
						servletRequest.getHeader("user-agent")));
				return generateBasicResponse("User Created Successfully", getRequestPath(servletRequest));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return generateBasicErrorResponse("Internal Server Error !!", getRequestPath(servletRequest),
				HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	@PostMapping("/login")
	@Transactional
	public IResponse login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest servletRequest) {

		User user = userService.getUserByEmail(loginRequest.getEmail());
		log.info("Login request received..",user);
		if (user != null && userService.validatePassword(loginRequest.getPassword(), user.getUserPassword())) {
			userService.generate2FAForUser(user);
			return generateBasicResponse("Login Success !!", getRequestPath(servletRequest));
		} else {
			return generateBasicErrorResponse("Login failed !!", getRequestPath(servletRequest), 400);
		}
	}

	@PostMapping("/verify-login")
	public IResponse verifyLogin(@Valid @RequestBody VerifyLoginRequest request, HttpServletRequest servletRequest) {
		User user = userService.getUserByEmail(request.getEmail());
		log.info("login verified..",user);
		if (user != null) {
			boolean passwordMatches = userService.validatePassword(request.getPassword(), user.getUserPassword());
			if (passwordMatches) {
				try {
					tokenService.verifyAndDeleteTokenForUser(request.getCode(), user, TokenTypes.TWO_FACTOR);
				} catch (InvalidTokenException e) {
					return generateBasicErrorResponse("Failed to validate please try again !!",
							getRequestPath(servletRequest), HttpStatus.NOT_ACCEPTABLE.value());
				}

				user.createAndSetNewSession();
				// Save user visit
				user.addVisit(new UserVisits(servletRequest.getHeader("X-Forwarded-For"),
						servletRequest.getHeader("user-agent")));
				userService.save(user);

				return generateBasicResponseWithToken(user, "Login verified successfully !! ", getRequestPath(servletRequest));
			}
		}
		return generateBasicErrorResponse("User not found !!", getRequestPath(servletRequest),
				HttpStatus.BAD_REQUEST.value());

	}

	@PostMapping("/verify-email")
	public IResponse verifyEmail(@Valid @RequestBody VerifyEmailRequest verifyEmailRequest,
			HttpServletRequest servletRequest) {
		User user = userService.verifyEmail(verifyEmailRequest.getToken());
		if (user != null) {
			return generateBasicResponseWithToken(user, "Email Verified SuccessFully !!", getRequestPath(servletRequest));
		}
		return generateBasicErrorResponse("Invalid Link !!", getRequestPath(servletRequest),
				HttpStatus.BAD_REQUEST.value());
	}

	private IResponse generateBasicResponseWithToken(User user, String message, String path) {
		IResponse resp = new BasicResponse(message, path, 200);
		resp.setToken(userService.generateJwtToken(user));
		return resp;
	}

	private IResponse generateBasicResponse(String message, String path) {
		IResponse resp = new BasicResponse(message, path, 200);
		return resp;
	}
	
	private IResponse generateBasicErrorResponse(String message, String path, int status) {
		IResponse resp = new BasicResponse(message, path, status);
		return resp;
	}

	private String getRequestPath(HttpServletRequest request) {
		return request.getRequestURL().toString();
	}
}
