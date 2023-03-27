package com.apiintegration.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.apiintegration.core.mail.Mail;
import com.apiintegration.core.mail.SendMail;
import com.apiintegration.core.model.Account;
import com.apiintegration.core.model.Token;
import com.apiintegration.core.model.User;
import com.apiintegration.core.utils.UserRole;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

	@Value("${app.mail.from}")
	private String from;

	@Value("${app.mail.from.pretty}")
	private String fromPretty;

	@Value("${app.url.logo}")
	private String appLogo;

	@Value("${app.url)")
	private String appUrl;

	@Autowired
	private final SendMail mailSender;

	public void send2faMail(User user, Token token) {
		try {

			Mail mail = new Mail();
			mail.setFrom(from);
			mail.setFromPretty(fromPretty);
			mail.setTo(user.getUserEmail());
			mail.setSubject("2FA code for APIIntegration Login");
			mail.setContent("login2fa_code.ftl");

			Map<String, String> model = new HashMap<>();
			model.put("logoUrl", appLogo);
			model.put("name", user.getUserFullName().toUpperCase());
			model.put("code", token.getToken());

			mail.setModel(model);

			mailSender.sendSimpleMessage(mail);

			log.debug("2FA mail sent to {} ,code : {}", user.getUserEmail(), token.getToken());
		} catch (Exception e) {
			log.error("Error processing SendTwoFactorEmailEvent!", e);
		}
	}

	public void sendVerifyEmailMail(User user, Token token) {
		try {
			Mail mail = new Mail();
			mail.setFrom(from);
			mail.setFromPretty(fromPretty);
			mail.setTo(user.getUserEmail());
			mail.setSubject("Verify Your Email On APIIntegration");
			mail.setContent("verify_email.ftl");

			Map<String, String> model = new HashMap<>();
			model.put("logoUrl", appLogo);
			model.put("name", user.getUserFullName().toUpperCase());
			model.put("url", appUrl + "/verify-email/?token=" + token.getToken());

			mail.setModel(model);

			mailSender.sendSimpleMessage(mail);

			log.debug("Verify-email mail sent to {}", user.getUserEmail());
		} catch (Exception e) {
			log.error("Error processing SendTwoFactorEmailEvent!", e);
		}

	}

	public void sendAccountInviteMail(String toEmail, User user, Token token) {
		try {
			Mail mail = new Mail();
			mail.setFrom(from);
			mail.setFromPretty(fromPretty);
			mail.setTo(toEmail);
			mail.setSubject("Account Invite from APIIntegration");
			mail.setContent("account_invite.ftl");

			Map<String, String> model = new HashMap<>();
			model.put("logoUrl", appLogo);
			model.put("name", user.getUserEmail());
			model.put("url", appUrl + "/account/join?token=" + token.getToken());

			mail.setModel(model);

			mailSender.sendSimpleMessage(mail);

			log.debug("Account Invite mail sent to {} by {}", toEmail, user.getUserEmail());
		} catch (Exception e) {
			log.error("Error processing SendTwoFactorEmailEvent!", e);
		}
	}

	public void sendResetPasswordMail(Token token, User user) {
		try {
			Mail mail = new Mail();
			mail.setFrom(from);
			mail.setFromPretty(fromPretty);
			mail.setTo(user.getUserEmail());
			mail.setSubject("Password Reset Request from APIIntegration");
			mail.setContent("reset_password.ftl");

			Map<String, String> model = new HashMap<>();
			model.put("logoUrl", appLogo);
			model.put("name", user.getUserEmail());
			model.put("url", appUrl + "/user/password?token=" + token.getToken());

			mail.setModel(model);

			mailSender.sendSimpleMessage(mail);

			log.debug("Reset-password mail sent to {}", user.getUserEmail());
		} catch (Exception e) {
			log.error("Error processing SendTwoFactorEmailEvent!", e);
		}
	}

	public void sendAccountDeleteRemainderMail(Account account, List<User> usersList, Token token) {
		try {

			// send confirmation mail to Account owner
			Mail mail = new Mail();

			mail.setFrom(from);
			mail.setFromPretty(fromPretty);
			mail.setTo(account.getUser().getUserEmail());
			mail.setSubject("Account Deletion Confirmation");
			mail.setContent("account_delete.ftl");

			Map<String, String> model = new HashMap<>();
			model.put("logoUrl", appLogo);
			model.put("name", account.getUser().getUserFullName());
			model.put("token", token.getData());

			mail.setModel(model);

			mailSender.sendSimpleMessage(mail);
			log.debug("Account Deletion Conformation mail sent to {}", account.getUser().getUserEmail());

			// send notification to all members in account
			for (User user : usersList) {
				mail = new Mail();

				mail.setFrom(from);
				mail.setFromPretty(fromPretty);
				mail.setTo(user.getUserEmail());
				mail.setSubject("Account Deletion Alert");
				mail.setContent("account_delete_alert.ftl");

				model = new HashMap<>();
				model.put("logoUrl", appLogo);
				model.put("name", user.getUserFullName());

				mail.setModel(model);

				mailSender.sendSimpleMessage(mail);
				log.debug("Account Deletion Remainder mail sent to {}", user.getUserEmail());
			}

		} catch (

		Exception e) {
			log.error("Error processing Remainder Mail for Account Delete !", e);
		}

	}
}
