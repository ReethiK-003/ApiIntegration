package com.apiintegration.core.service;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.apiintegration.core.exception.DuplicateEntryException;
import com.apiintegration.core.model.Account;
import com.apiintegration.core.model.User;
import com.apiintegration.core.repo.AccountRepo;
import com.apiintegration.core.request.AddProjectToUserRequest;
import com.apiintegration.core.request.CreateAccountRequest;
import com.apiintegration.core.request.InviteUserRequest;
import com.apiintegration.core.utils.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepo accountRepo;
	private final UserService userService;

	public User createNewAccount(CreateAccountRequest request, User user) {

		if (!accountRepo.existsByAccountName(request.getAccountName())) {
			Account account = new Account();
			account.setAccountName(request.getAccountName());
			account.setAccountDescription(request.getAccountDescription());
			account.setUser(user);
			account.setUsersCount(1L);

			Account dbAccount = accountRepo.save(account);
			return setAccountToUser(dbAccount);
		} else {
			throw new DuplicateEntryException("Account with name already exists please try again with new name.");
		}
	}

	public void inviteNewUser(InviteUserRequest request, User user) {

		if (userService.emailExists(request.getEmail())) {
			throw new DuplicateEntryException("User with email already enrolled in Account.");
		}
		if (!UserRole.isValidRole(request.getRole())) {
			throw new DuplicateEntryException("Invalid User Role type possible values are {OWNER,LEAD,SUPERDEV,DEV}.");
		}
		userService.sendAccountInviteMail(request, user);
	}

	public User addProjectToUser(AddProjectToUserRequest request, User user) {
		return userService.addProjectToUser(request, user);
	}

	private User setAccountToUser(Account account) {
		Account dbAccount;
		try {
			dbAccount = getAccountById(account.getId());
			if (dbAccount != null) {
				User user = dbAccount.getUser();
				user.setAccount(dbAccount);
				user.setUserRole(UserRole.OWNER);

				return userService.save(user);
			} else {
				throw new RuntimeException("Failed to set Account to User for new Account Creation");
			}
		} catch (NotFoundException e) {
			throw new RuntimeException("Failed to save User for new Account Creation");
		}
	}

	@Transactional
	public User validateAndJoinUser(String req, User user) throws JsonMappingException, JsonProcessingException, NotFoundException {
		 userService.addAccountToUserWithToken(user, req);
		 Account account = user.getAccount();
		 account.increasUsersCount();
		 save(account);
		 return user;
	}

	public Account getAccountById(Long id) throws NotFoundException {
		return accountRepo.findById(id)
				.orElseThrow(() -> new NotFoundException("Account Not found Please try again !!"));
	}
	
	public Account save(Account account) {
		return accountRepo.save(account);
	}

}