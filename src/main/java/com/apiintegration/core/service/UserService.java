package com.apiintegration.core.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.apiintegration.core.exception.DuplicateEntryException;
import com.apiintegration.core.exception.InvalidTokenException;
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
import com.apiintegration.core.request.InviteUserRequest;
import com.apiintegration.core.request.SignupRequest;
import com.apiintegration.core.utils.TokenTypes;
import com.apiintegration.core.utils.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javassist.NotFoundException;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;
	@Autowired
	private MailService mailService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private AccountService accountService;
	@Autowired
	private ProjectService projectService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Transactional
	public User createNewUser(SignupRequest request) throws DuplicateEntryException {

		if (emailExists(request.getEmail())) {
			System.out.println("Email already exist please try with different email :" + request.getEmail());
			return null;
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

	public User addAccountToUserWithToken(User user, String tokenVal) throws NotFoundException {

		Token token = tokenService.findByTokenAndType(tokenVal, TokenTypes.ACCOUNT_INVITE);

		try {
			if (token != null) {
				InviteUserRequest tokenData = objectMapper.readValue(token.getData(), InviteUserRequest.class);
				Account account = accountService.getAccountById(tokenData.getAccountId());
				List<Project> projects = new LinkedList<>();
				for (Long projectId : tokenData.getProjectsId()) {
					projects.add(projectService.getProjectbyId(projectId));
				}

				if (tokenData.getEmail().equals(user.getUserEmail())) {
					User dbUser = user;
					dbUser.setAccount(account);
					dbUser.setUserRole(tokenData.getRole());
					dbUser = addProjects(user, projects, token.getUser());

					return save(dbUser);
				}
				throw new InvalidTokenException(
						"User with email has no access to join the Account with this token " + user.getUserEmail());
			}
			throw new InvalidTokenException("Provided Token not found please try again !!.");

		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error occured while processing token data as JSON type .. Try again !!");
		}
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

	public void sendEmailVerifyMail(User user) {
		Token token = tokenService.createMailVerificationToken(user);
		user.addToken(token);
		save(user);
		mailService.sendVerifyEmailMail(user, token);
	}

	public void sendAccountInviteMail(InviteUserRequest request, User user) {
		try {
			String data = objectMapper.writeValueAsString(request);
			Token token = tokenService.createAccountInviteToken(user, data);

			user.addToken(token);
			save(user);
			mailService.sendAccountInviteMail(request.getEmail(), user, token);
		} catch (Exception e) {
			throw new RuntimeException("Failed to send Account invite to User with exception : ", e);
		}
	}

	private User addProjects(User user, List<Project> projects, User createdBy) {

		for (Project project : projects) {
			RelUserProject relUserProject = new RelUserProject();
			relUserProject.setProject(project);
			relUserProject.setCreatedUser(createdBy);
			user.addProject(relUserProject);
		}
		return user;
	}

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
	public User addProjectToUser(AddProjectToUserRequest request, User createdBy) {

		User user = getUserById(request.getAddToUserId());
		if (user != null) {
			return save(addProjects(user, request.getProjectsList(), createdBy));
		}

		throw new DuplicateEntryException("User not found please try again !!");
	}

	public List<Project> listAllUserProjects(User user) {
		Set<RelUserProject> relProjects = user.getProjects();
		List<Project> projects = new LinkedList<Project>();

		for (RelUserProject it : relProjects) {
			projects.add(it.getProject());
		}
		return projects;
	}

	public String generateJwtToken(User user) {
		HashMap<String, Object> claims = new HashMap<>();
		claims.put("vem", user.getVerifiedEmail());
		claims.put("jti", user.getSession());
		claims.put("name", user.getUserFullName());
		claims.put("role", user.getUserRole());

		return jwtTokenUtil.generateToken(new UserDetailsImpl(user), claims);
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

	public User getUserById(Long id) {
		return userRepo.findById(id).orElse(null);
	}

	public User getUserByEmail(String email) {
		return userRepo.findByUserEmail(email);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = getUserByEmail(username);
		if (user != null) {
			return new UserDetailsImpl(user);
		}
		throw new UsernameNotFoundException("No user found with e-mail: " + username);
	}
}