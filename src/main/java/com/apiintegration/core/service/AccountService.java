package com.apiintegration.core.service;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.mail.MethodNotSupportedException;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.apiintegration.core.exception.DuplicateEntryException;
import com.apiintegration.core.exception.UserNotFoundException;
import com.apiintegration.core.model.Account;
import com.apiintegration.core.model.Project;
import com.apiintegration.core.model.RelUserProject;
import com.apiintegration.core.model.User;
import com.apiintegration.core.repo.AccountRepo;
import com.apiintegration.core.request.AddProjectToUserRequest;
import com.apiintegration.core.request.CreateAccountRequest;
import com.apiintegration.core.request.InviteUserRequest;
import com.apiintegration.core.request.UpdateUserRequest;
import com.apiintegration.core.utils.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepo accountRepo;
	private final UserService userService;

	public Account createNewAccount(CreateAccountRequest request, User user)
			throws MethodNotSupportedException, NotFoundException {

		if (user.getAccount() != null) {
			throw new MethodNotSupportedException("Already present in another Account !!");
		}
		if (!accountRepo.existsByAccountName(request.getAccountName())) {
			log.info("Creating new account for User .. ", user);
			Account account = new Account();
			account.setAccountName(request.getAccountName());
			account.setAccountDescription(request.getAccountDescription());
			account.setUser(user);
			account.setUsersCount(1L);

			accountRepo.save(account);

			return setAccountToUser(account).getAccount();
		} else {
			throw new DuplicateEntryException("Account with name already exists please try again with new name.");
		}
	}

	public void inviteNewUser(InviteUserRequest request, User user) throws UserNotFoundException {
		if (validateEmailToInvite(request.getEmail())) {
			if (!UserRole.isValidRole(request.getRole())) {
				throw new DuplicateEntryException(
						"Invalid User Role possible values are {LEAD,SUPERDEV,DEV}.");
			}
			userService.sendAccountInviteMail(request, user);
		}
		throw new DuplicateEntryException("User with email already enrolled in Account.");

	}

	private boolean validateEmailToInvite(String email) throws UserNotFoundException {

		if (userService.emailExists(email)) {
			return userService.getUserByEmail(email).getAccount() == null;
		}
		return true;
	}

	@Transactional
	public User validateAndJoinUser(String req, User user)
			throws JsonMappingException, JsonProcessingException, NotFoundException {

		userService.addAccountToUserWithToken(user, req);

		Account account = user.getAccount();
		account.increasUsersCount();
		save(account);
		return user;
	}
//	public User updateUser(UpdateUserRequest request , User mainUser) {
//		User user = userService.getUserById(request.getUserId());
//		if(user != null && (user.getAccount() == mainUser.getAccount())) {
//			Optional.ofNullable(request.getRole()).ifPresent(user::setUserRole);
//			List<Project> projects = request.getProjects();
//			user = checkAndUpdateProjectsForUser(projects , user);
//			
//			userService.save(user);
//		}
//	}

	public User addProjectToUser(AddProjectToUserRequest request, User user) throws UserNotFoundException {
		return userService.addProjectToUser(request, user);
	}
	
//	public User checkAndUpdateProjectsForUser(List<Project> projects , User user) {
//		
//		Set<RelUserProject> existingProjects = user.getProjects();
//		for (Project project : projects) {
//			project.is
//		}
//	}

	private User setAccountToUser(Account account) throws NotFoundException {

		log.info("Updating User for new Account Created .. ", account);
		Account dbAccount = getAccountById(account.getId());

		User user = dbAccount.getUser();
		user.setAccount(dbAccount);
		user.setUserRole(UserRole.OWNER);

		return userService.save(user);
	}

	public Account getAccountById(Long id) throws NotFoundException {
		return accountRepo.findById(id)
				.orElseThrow(() -> new NotFoundException("Account Not found Please try again !!"));
	}

	public Account save(Account account) {
		return accountRepo.save(account);
	}

}