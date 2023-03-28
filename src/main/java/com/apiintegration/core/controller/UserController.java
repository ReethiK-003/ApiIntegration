package com.apiintegration.core.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiintegration.core.exception.UserNotFoundException;
import com.apiintegration.core.jwt.JwtTokenUtil;
import com.apiintegration.core.model.User;
import com.apiintegration.core.model.UserVisits;
import com.apiintegration.core.request.ChangePasswordRequest;
import com.apiintegration.core.request.ForgotPasswordRequest;
import com.apiintegration.core.request.LoginRequest;
import com.apiintegration.core.request.RefreshTokenRequest;
import com.apiintegration.core.request.ResetPasswordRequest;
import com.apiintegration.core.request.SignupRequest;
import com.apiintegration.core.request.VerifyEmailRequest;
import com.apiintegration.core.request.VerifyLoginRequest;
import com.apiintegration.core.response.BasicResponse;
import com.apiintegration.core.response.DataResponse;
import com.apiintegration.core.response.IDataResponse;
import com.apiintegration.core.response.IResponse;
import com.apiintegration.core.service.TokenService;
import com.apiintegration.core.service.UserService;
import com.apiintegration.core.utils.TokenTypes;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final TokenService tokenService;
	private final JwtTokenUtil jwtService;

	@GetMapping("/me")
	public IResponse getUser(@RequestAttribute User user, HttpServletRequest servletRequest) {
		try {
			User _user = userService.getUserByEmail(user.getUserEmail());
			return new DataResponse(_user, "success", getRequestPath(servletRequest), 200);
		} catch (Exception e) {
			return new BasicResponse(null, null, 400);
		}
	}

	@PostMapping("/signup")
	public IResponse signup(@Valid @RequestBody SignupRequest signupRequest, HttpServletRequest servletRequest) {

		try {
			User newUser = userService.createNewUser(signupRequest);

			userService.saveUserVisit(newUser, new UserVisits(servletRequest.getHeader("X-Forwarded-For"),
					servletRequest.getHeader("user-agent")));

			return generateBasicResponse("User created successfully !!", getRequestPath(servletRequest));
		} catch (Exception e) {
			return generateBasicErrorResponse(e.getMessage(), getRequestPath(servletRequest));
		}
	}

	@PostMapping("/login")
	public IResponse login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest servletRequest) {

		try {
			User user = userService.getUserByEmail(loginRequest.getEmail());

			if (userService.validatePassword(loginRequest.getPassword(), user.getUserPassword())) {

				userService.generate2FAForUser(user);

				return generateBasicResponse("Login success !!", getRequestPath(servletRequest));
			}
			return generateBasicErrorResponse("Login failed wrong password !!", getRequestPath(servletRequest));
		} catch (UserNotFoundException e) {
			return generateBasicErrorResponse(e.getMessage(), getRequestPath(servletRequest));
		}
	}

	@PostMapping("/verify-login")
	public IResponse verifyLogin(@Valid @RequestBody VerifyLoginRequest request, HttpServletRequest servletRequest) {

		try {
			User user = userService.getUserByEmail(request.getEmail());

			if (userService.validatePassword(request.getPassword(), user.getUserPassword())) {

				tokenService.verifyAndDeleteTokenForUser(request.getCode(), user, TokenTypes.TWO_FACTOR);

				user.createAndSetNewSession();
				// Save user visit
				user.addVisit(new UserVisits(servletRequest.getHeader("X-Forwarded-For"),
						servletRequest.getHeader("user-agent")));
				userService.save(user);

				return generateBasicResponseWithToken(user, "Login verified successfully !! ",
						getRequestPath(servletRequest));
			}
			return generateBasicErrorResponse("Login failed wrong password !!", getRequestPath(servletRequest));
		} catch (Exception e) {
			return generateBasicErrorResponse(e.getMessage(), getRequestPath(servletRequest));
		}
	}

	@PostMapping("/verify-email")
	public IResponse verifyEmail(@Valid @RequestBody VerifyEmailRequest verifyEmailRequest,
			HttpServletRequest servletRequest) {

		try {
			User user = userService.verifyEmail(verifyEmailRequest.getToken());

			return generateBasicResponseWithToken(user, "Email Verified SuccessFully !!",
					getRequestPath(servletRequest));
		} catch (Exception e) {
			return generateBasicErrorResponse(e.getMessage(), getRequestPath(servletRequest));
		}
	}

	@PostMapping("/forgot-password")
	public IResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request,
			HttpServletRequest servletRequest) {

		if (userService.forgotPasswordRequest(request.getEmail())) {

			return generateBasicResponse("Reset password link sent succesfully !!", getRequestPath(servletRequest));
		}
		return generateBasicErrorResponse("Failed to send Reset-password !!", getRequestPath(servletRequest));
	}

	@PostMapping("/reset-password")
	public IResponse setPassword(@Valid @RequestBody ResetPasswordRequest request, HttpServletRequest servletRequest) {

		try {
			User user = userService.resetPassword(request);
			return generateBasicResponseWithToken(user, "Password Reset success !!", getRequestPath(servletRequest));
		} catch (Exception e) {
			return generateBasicErrorResponse(e.getMessage(), getRequestPath(servletRequest));
		}
	}

	@PostMapping("/change-password")
	public IResponse changePassword(@Valid @RequestBody ChangePasswordRequest request, @RequestAttribute User user,
			HttpServletRequest servletRequest) {

		try {
			User newUser = userService.changePassword(request, user);
			return generateBasicResponseWithToken(newUser, "Password Reset success !!", getRequestPath(servletRequest));
		} catch (Exception e) {
			return generateBasicErrorResponse(e.getMessage(), getRequestPath(servletRequest));
		}
	}

	@PostMapping("/refresh-token")
	public IResponse refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
			HttpServletRequest servletRequest) {

		try {
			User user = userService.getUserByEmail(jwtService.getUsernameFromToken(refreshTokenRequest.getToken()));
			return generateBasicResponseWithToken(user, "Token refreshed succesfully !!",
					getRequestPath(servletRequest));
		} catch (Exception e) {
			return generateBasicErrorResponse(e.getLocalizedMessage(), getRequestPath(servletRequest));
		}
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

	private IResponse generateBasicErrorResponse(String message, String path) {
		IResponse resp = new BasicResponse(message, path, HttpStatus.BAD_REQUEST.value());
		return resp;
	}

	private String getRequestPath(HttpServletRequest request) {
		return request.getRequestURL().toString();
	}
}
