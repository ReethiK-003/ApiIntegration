package com.apiintegration.core.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiintegration.core.exception.DuplicateEntryException;
import com.apiintegration.core.exception.InvalidTokenException;
import com.apiintegration.core.exception.UserNotFoundException;
import com.apiintegration.core.jwt.JwtTokenUtil;
import com.apiintegration.core.jwt.UserDetailsImpl;
import com.apiintegration.core.model.Account;
import com.apiintegration.core.model.Project;
import com.apiintegration.core.model.RelUserProject;
import com.apiintegration.core.model.Token;
import com.apiintegration.core.model.User;
import com.apiintegration.core.model.UserVisits;
import com.apiintegration.core.repo.UserRepo;
import com.apiintegration.core.request.AddProjectToUserRequest;
import com.apiintegration.core.request.ChangePasswordRequest;
import com.apiintegration.core.request.InviteUserRequest;
import com.apiintegration.core.request.ResetPasswordRequest;
import com.apiintegration.core.request.SignupRequest;
import com.apiintegration.core.utils.TokenTypes;
import com.apiintegration.core.utils.UserRole;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService implements UserDetailsService {

	private UserRepo userRepo;

	private MailService mailService;

	private PasswordEncoder passwordEncoder;

	private TokenService tokenService;

	private JwtTokenUtil jwtTokenUtil;

	@Transactional
	public User createNewUser(SignupRequest request) throws DuplicateEntryException {

		if (emailExists(request.getEmail())) {
			throw new DuplicateEntryException("User already exist with email !");
		}
		User user = new User();
		user.setUserFullName(request.getFirstName() + " " + request.getLastName());
		user.setUserEmail(request.getEmail());
		user.setUserPassword(passwordEncoder.encode(request.getConfirmPassword()));
		user.createAndSetNewSession();
		user.setUserRole(UserRole.USER);
		User savedUser = save(user);

		sendEmailVerifyMail(savedUser);

		return user;
	}

	@Transactional
	public void generate2FAForUser(User user) {
		Token token = tokenService.createTwoFactorToken(user);

		user.addToken(token);
		save(user);
		try {
			mailService.send2faMail(user, token);
		} catch (Exception e) {
			throw new RuntimeException("Failed to send 2FA code for User with exception : ", e);
		}
	}

	@Transactional
	public void sendEmailVerifyMail(User user) {
		Token token = tokenService.createMailVerificationToken(user);
		user.addToken(token);
		save(user);
		mailService.sendVerifyEmailMail(user, token);
	}

	@Transactional
	public void sendAccountInviteMail(InviteUserRequest request, User user, String data) {
		try {
			Token token = tokenService.createAccountInviteToken(user, data);

			user.addToken(token);
			save(user);
			mailService.sendAccountInviteMail(request.getEmail(), user, token);
			log.debug("Account Invite sent to new user with email {} by {} to {}", request.getEmail(),
					user.getUserEmail(), request.getAccountId());
		} catch (Exception e) {
			log.error("Failed to send Account invite to User with email {},by {}", request.getEmail(),
					user.getUserEmail());
			throw new RuntimeException(e);
		}
	}

	public User addProjects(User user, List<Project> projects, User createdBy) {

		for (Project project : projects) {
			RelUserProject relUserProject = new RelUserProject();
			relUserProject.setProject(project);
			relUserProject.setCreatedUser(createdBy);
			user.addProject(relUserProject);
		}
		return user;
	}

	@Transactional
	public User verifyEmail(String requestToken) {
		User user = null;

		Token token = tokenService.findByTokenAndType(requestToken, TokenTypes.CONFIRM_EMAIL);

		if (token != null) {
			if (token.getUser().getVerifiedEmail()) {
				throw new RuntimeException("Your email is already verified.");
			}
			if (token.hasExpired()) {
				throw new InvalidTokenException("This link has expired!");
			}
			user = token.getUser();
			if (!user.getVerifiedEmail()) {
				user.setVerifiedEmail(Boolean.TRUE);
				save(user);
			}
			tokenService.expireToken(token);
		}
		return user;
	}

	@Transactional
	public User addProjectToUser(AddProjectToUserRequest request, User createdBy) throws UserNotFoundException {
		User user = getUserById(request.getAddToUserId());
		return save(addProjects(user, request.getProjectsList(), createdBy));
	}

	public String generateJwtToken(User user) {
		HashMap<String, Object> claims = new HashMap<>();
		claims.put("vem", user.getVerifiedEmail());
		claims.put("jti", user.getSession());
		claims.put("name", user.getUserFullName());
		claims.put("role", user.getUserRole());

		return jwtTokenUtil.generateToken(new UserDetailsImpl(user), claims);
	}

	public boolean forgotPasswordRequest(String email) {
		try {
			User user = getUserByEmail(email);
			user.setUserPassword(null);
			Token token = tokenService.createResetPasswordToken(user);
			mailService.sendResetPasswordMail(token, user);
			user.addToken(token);
			save(user);
			return true;
		} catch (Exception e) {
			log.debug("Failed to process reset-password request for email{}", email);
			return false;
		}
	}

	public User resetPassword(ResetPasswordRequest request) {
		try {
			Token token = tokenService.findByTokenAndType(request.getToken(), TokenTypes.RESET_PASSWORD);
			User user = token.getUser();

			user.setUserPassword(passwordEncoder.encode(request.getPassword()));
			user.createAndSetNewSession();
			token.expireNow();
			save(user);
			return user;
		} catch (Exception e) {
			throw new DuplicateEntryException(e.getMessage());
		}
	}

	public User changePassword(ChangePasswordRequest request, User user) throws UserNotFoundException {
		try {
			User newUser = getUserByEmail(user.getUserEmail());
			if (passwordEncoder.matches(request.getCurrentPassword(), newUser.getUserPassword())) {
				newUser.setUserPassword(passwordEncoder.encode(request.getNewPassword()));
				newUser.createAndSetNewSession();

				return save(newUser);
			}
			throw new AccessDeniedException("Invalid password try again !!");
		} catch (Exception e) {
			throw new UserNotFoundException();
		}
	}

	@Transactional
	public void saveUserVisit(User user, UserVisits userVisit) {
		user.addVisit(userVisit);
		userRepo.save(user);
	}

	public boolean emailExists(String email) {
		return userRepo.existsByUserEmail(email);
	}

	public boolean validatePassword(CharSequence rawPassword, String password) {
		return passwordEncoder.matches(rawPassword, password);
	}

	public User save(User user) {
		return userRepo.save(user);
	}

	public User saveAndFlush(User user) {
		return userRepo.saveAndFlush(user);
	}

	public User getUserById(Long id) throws UserNotFoundException {
		return userRepo.findById(id).orElseThrow(() -> new UserNotFoundException());
	}

	public User getUserByEmail(String email) throws UserNotFoundException {
		return userRepo.findByUserEmail(email).orElseThrow(() -> new UserNotFoundException());
	}

	public List<User> getAllUsers(Account account) {
		return userRepo.findByAccount(account);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			User user = getUserByEmail(username);
			return new UserDetailsImpl(user);
		} catch (UserNotFoundException e) {
			log.debug("User Not found for JWT authentication for email {}", username);
			return null;
		}
	}

	@Autowired
	private void setUserRepo(UserRepo userRepo) {
		this.userRepo = userRepo;
	}

	@Autowired
	private void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	@Autowired
	private void PasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Autowired
	private void TokenService(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Autowired
	private void JwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
		this.jwtTokenUtil = jwtTokenUtil;
	}
}