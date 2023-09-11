package com.apiintegration.core.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.mail.MethodNotSupportedException;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.apiintegration.core.exception.DuplicateEntryException;
import com.apiintegration.core.exception.InvalidOperationException;
import com.apiintegration.core.exception.InvalidTokenException;
import com.apiintegration.core.exception.UserNotFoundException;
import com.apiintegration.core.model.Account;
import com.apiintegration.core.model.Project;
import com.apiintegration.core.model.RelUserProject;
import com.apiintegration.core.model.Token;
import com.apiintegration.core.model.User;
import com.apiintegration.core.repo.AccountRepo;
import com.apiintegration.core.request.AddProjectToUserRequest;
import com.apiintegration.core.request.CreateAccountRequest;
import com.apiintegration.core.request.InviteUserRequest;
import com.apiintegration.core.request.UpdateAccountRequest;
import com.apiintegration.core.request.UpdateUserRequest;
import com.apiintegration.core.utils.TokenTypes;
import com.apiintegration.core.utils.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javassist.NotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepo accountRepo;
	private final UserService userService;
	private final MailService mailService;
	private final TokenService tokenService;
	private final ProjectService projectService;
	private final EntityManager entityManager;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Transactional
	public Account createNewAccount(CreateAccountRequest request, User user)
			throws MethodNotSupportedException, NotFoundException {
		if (user.getAccount() != null) {
			throw new MethodNotSupportedException("Already present in another Account !!");
		}
		if (accountRepo.findByAccountName(request.getAccountName()) == null) {
			log.info("Creating new account for User .. ", user);
			Account account = new Account();
			account.setAccountName(request.getAccountName());
			account.setAccountDescription(request.getAccountDescription());
			account.setUser(user);
			account.setUsersCount(1L);

			accountRepo.save(account);
			
			log.info("New Account created with name {}, by {}", account.getAccountName() , user.getUserEmail());
			
			return setAccountToUser(account).getAccount();
		} else {
			log.debug("Account creation failed for user : {} ", user);
			
			throw new DuplicateEntryException("Account with name already exists please try again with new name.");
		}
	}

	public Account updateAccount(UpdateAccountRequest request) throws NotFoundException {

		Account account = getAccountById(request.getAccountId());

		Optional.ofNullable(request.getAccountName()).ifPresent(account::setAccountName);
		Optional.ofNullable(request.getAccountDescription()).ifPresent(account::setAccountDescription);

		log.info("Account updated : {}", account);
		
		return save(account);

	}

	public void inviteNewUser(InviteUserRequest request, User user)
			throws UserNotFoundException, JsonProcessingException {
		if (validateEmailToInvite(request.getEmail())) {
			if (!UserRole.isValidRole(request.getRole())) {
				throw new DuplicateEntryException("Invalid User Role possible values are {LEAD,SUPERDEV,DEV}.");
			}
			String data = objectMapper.writeValueAsString(request);
			userService.sendAccountInviteMail(request, user, data);
		} else {
			throw new DuplicateEntryException("User with email already enrolled in Account.");
		}
	}

	private boolean validateEmailToInvite(String email) throws UserNotFoundException {

		if (userService.emailExists(email)) {
			return UserRole.USER.equals(userService.getUserByEmail(email).getUserRole());
		}
		return true;
	}

	public Account validateAndJoinUser(String req, User user)
			throws JsonMappingException, JsonProcessingException, NotFoundException, InvalidTokenException {

		User savedUser = addAccountToUserWithToken(user, req);
		Account account = savedUser.getAccount();
		account.increasUsersCount();

		return save(account);
	}

	@Transactional
	public User addAccountToUserWithToken(User mainUser, String tokenVal)
			throws JsonMappingException, JsonProcessingException, NotFoundException {

		Token token = tokenService.findByTokenAndType(tokenVal, TokenTypes.ACCOUNT_INVITE);
		if (token != null) {
			InviteUserRequest tokenData = objectMapper.readValue(token.getData(), InviteUserRequest.class);
			User user = userService.getUserByEmail(tokenData.getEmail());
			Account account = getAccountById(tokenData.getAccountId());
			List<Project> projects = new LinkedList<>();

			for (Long projectId : tokenData.getProjectsId()) {
				projects.add(projectService.getProject(projectId));
			}

			if (tokenData.getEmail().equals(user.getUserEmail())) {
				user.setAccount(account);
				user.setUserRole(tokenData.getRole());

				user = userService.addProjects(user, projects, token.getUser());
				
				log.debug("New member joined the account with email :{} as role :{} in {}", user.getUserEmail(),
						user.getUserRole(), account.getAccountName());
				
				return userService.save(user);
			} else {
				throw new InvalidTokenException(
						"User with email has no access to join the Account with this token " + user.getUserEmail());
			}
		} else {
			throw new InvalidTokenException("Provided Token not found please try again !!.");
		}
	}

	@Transactional
	public User updateUser(UpdateUserRequest request, User mainUser)
			throws NotFoundException, DuplicateEntryException, InvalidOperationException {
		User user = userService.getUserById(request.getUserId());
		String role = request.getRole();
		if (!UserRole.isValidRole(request.getRole())) {
			throw new DuplicateEntryException("Invalid User Role possible values are {LEAD,SUPERDEV,DEV}.");
		}

		if (user != null && (user.getAccount().getId().equals(mainUser.getAccount().getId()))) {
			Optional.ofNullable(role).ifPresent(user::setUserRole);
			List<Project> projects = new LinkedList<>();
			for (Long pId : request.getProjectsId()) {
				projects.add(projectService.getProject(pId));
			}
			user = checkAndUpdateProjectsForUser(projects, user, mainUser);
			return userService.save(user);
		} else {
			throw new InvalidOperationException("User not belongs to the same account .. ");
		}
	}

	public User addProjectToUser(AddProjectToUserRequest request, User user) throws UserNotFoundException {
		return userService.addProjectToUser(request, user);
	}

	@Transactional
	public User checkAndUpdateProjectsForUser(List<Project> projects, User user, User mainUser) {

		List<Project> existingProjects = user.getProjects();

		// removes the projects in existing list if not present in new list.
		existingProjects.retainAll(projects);

		// adds new projects in existing list from new list if not present already.
		for (Project newPro : projects) {
			if (!existingProjects.contains(newPro)) {
				RelUserProject relUserProject = new RelUserProject();
				relUserProject.setProject(newPro);
				relUserProject.setCreatedUser(mainUser);
				user.addProject(relUserProject);
			}
		}
		return user;
	}

	@Transactional
	public Account removeUser(Long userId) throws NotFoundException {

		User user = userService.getUserById(userId);
		Account account = getAccountById(user.getAccount().getId());

		entityManager.lock(user, LockModeType.PESSIMISTIC_WRITE);
		entityManager.lock(account, LockModeType.PESSIMISTIC_WRITE);

		user.setAccount(null);
		user.getProjects().clear();
		user.setUserRole(UserRole.USER);

		userService.save(user);

		account.decreaseUsersCount();
		return save(account);
	}

	@Transactional
	public void deleteAccountRequest(User user) throws NotFoundException {

		Account account = user.getAccount();
		List<User> usersList = userService.getAllUsers(account);
		Token token = tokenService.createDeleteAccountToken(user);
		user.addToken(token);
		userService.save(user);
		
		log.info("Account Delete Requested.");
		mailService.sendAccountDeleteRemainderMail(account, usersList, token);
	}

	@Transactional
	public void deleteAccountAndSanitize(String tData) {
		Token token = tokenService.findByTokenAndType(tData, TokenTypes.DELETE_ACCOUNT);
		if (token != null) {
			Account account = token.getUser().getAccount();
			List<User> usersList = userService.getAllUsers(account);

			for (User user : usersList) {
				user.setAccount(null);
				user.setUserRole(UserRole.USER);
				user.getProjects().clear();

				userService.save(user);
			}
			accountRepo.delete(account);
			log.info("Account and its Associations Deleted and Users seperated :{}", account);
		}
	}

	private User setAccountToUser(Account account) throws NotFoundException {

		log.info("Updating User for new Account Created .. ", account);
		Account dbAccount = getAccountById(account.getId());

		User user = dbAccount.getUser();
		user.setAccount(dbAccount);
		user.setUserRole(UserRole.OWNER);

		return userService.save(user);
	}

	public List<Project> getAllProjects(Account account) {
		return projectService.getAllProjectsByAccount(account);
	}

	public List<User> getAllUsers(Account account) {
		return userService.getAllUsers(account);
	}

	public Account getAccountById(Long id) throws NotFoundException {
		return accountRepo.findById(id)
				.orElseThrow(() -> new NotFoundException("Account Not found Please try again !!"));
	}

	public Account save(Account account) {
		return accountRepo.save(account);
	}
}