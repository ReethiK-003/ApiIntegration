package com.apiintegration.core.service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiintegration.core.exception.EntryNotFoundException;
import com.apiintegration.core.exception.InvalidTokenException;
import com.apiintegration.core.model.Token;
import com.apiintegration.core.model.User;
import com.apiintegration.core.repo.TokenRepo;
import com.apiintegration.core.utils.AccountInviteToken;
import com.apiintegration.core.utils.ConfirmEmailToken;
import com.apiintegration.core.utils.DeleteAccountToken;
import com.apiintegration.core.utils.ResetPasswordToken;
import com.apiintegration.core.utils.TokenTypes;
import com.apiintegration.core.utils.TwoFactorToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

	private final TokenRepo tokenRepo;

	@Transactional
	public Token findByTokenAndType(String tokenString, String type) {
		Token token = tokenRepo.findByTokenAndType(tokenString, type)
				.orElseThrow(() -> new EntryNotFoundException("Invalid Token or Token not found contact Support"));

		if (token.hasExpired()) {
			throw new InvalidTokenException("This link has expired.");
		}

		return token;
	}

	@Transactional
	public Token findByTokenAndEmailAndType(String tokenString, String email, String type) {
		Token token = tokenRepo.findByTokenAndType(tokenString, type)
				.orElseThrow(() -> new EntryNotFoundException("Invalid Token or Token not found contact Support"));

		if (token.hasExpired()) {
			throw new InvalidTokenException("This link has expired.");
		}
		if (token.getUser().getUserEmail().equalsIgnoreCase(email)) {
			return token;
		} else {
			throw new InvalidTokenException("This link is invalid for the e-mail address provided.");
		}
	}

	public void verifyAndDeleteTokenForUser(String tokenString, User user, String type) {
		Token token = tokenRepo.findByTokenAndTypeAndUser(tokenString, type, user)
				.orElseThrow(() -> new EntryNotFoundException("Invalid Token or Token not found contact Support"));

		if (token.hasExpired()) {
			throw new InvalidTokenException("Token has expired.");
		}

		tokenRepo.delete(token);
	}

	public void expireToken(Token token) {
		try {
			token.expireNow();
			tokenRepo.saveAndFlush(token);
		} catch (Exception e) {
//			log.warn("Failed to expire token: {}", token);
		}
	}

	public Token createTwoFactorToken(User user) {
		TwoFactorToken twoFactorToken = new TwoFactorToken();
		twoFactorToken.setUser(user);
		twoFactorToken.setToken(RandomStringUtils.random(6, false, true));
		twoFactorToken.setExpiresAt(Token.calculateExpiryDate(TwoFactorToken.EXPIRATION));

		return twoFactorToken;
	}

	public Token createMailVerificationToken(User user) {
		ConfirmEmailToken confirmEmailToken = new ConfirmEmailToken();
		confirmEmailToken.setUser(user);
		confirmEmailToken.setToken(RandomStringUtils.random(32, 0, 0, true, true, null, new SecureRandom()));
		confirmEmailToken.setExpiresAt(Token.calculateExpiryDate(ConfirmEmailToken.EXPIRATION));

		return confirmEmailToken;
	}

	public Token createResetPasswordToken(User user) {
		expireLastToken(user);
		ResetPasswordToken confirmEmailToken = new ResetPasswordToken();
		confirmEmailToken.setUser(user);
		confirmEmailToken.setToken(RandomStringUtils.random(32, 0, 0, true, true, null, new SecureRandom()));
		confirmEmailToken.setExpiresAt(Token.calculateExpiryDate(ResetPasswordToken.EXPIRATION));

		return confirmEmailToken;
	}

	public Token createAccountInviteToken(User user, String data) {
		AccountInviteToken accountInviteToken = new AccountInviteToken();
		accountInviteToken.setUser(user);
		accountInviteToken.setToken(RandomStringUtils.random(32, 0, 0, true, true, null, new SecureRandom()));
		accountInviteToken.setExpiresAt(Token.calculateExpiryDate(AccountInviteToken.EXPIRATION));
		accountInviteToken.setData(data);

		return accountInviteToken;
	}

	public Token createDeleteAccountToken(User user) {
		DeleteAccountToken deleteAccountToken = new DeleteAccountToken();
		deleteAccountToken.setUser(user);
		deleteAccountToken.setToken(RandomStringUtils.random(32, 0, 0, true, true, null, new SecureRandom()));
		deleteAccountToken.setExpiresAt(Token.calculateExpiryDate(DeleteAccountToken.EXPIRATION));

		return deleteAccountToken;
	}

	@Transactional
	public void deleteExpired() {
		tokenRepo.deleteExpiredTokens(new Date());
	}

	@Scheduled(cron = "0 * * * * ?")
	public void scheduledDeleteExpiredTokens() {
		deleteExpired();
	}

	public void expireLastToken(User user) {
		Token token = tokenRepo.findByTypeAndUserAndExpiresAtAfter(TokenTypes.RESET_PASSWORD, user, new Date())
				.orElse(null);
		if (!Objects.isNull(token)) {
			expireToken(token);
		}
	}

}
