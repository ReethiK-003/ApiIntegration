package com.apiintegration.core.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.apiintegration.core.mail.Mail;
import com.apiintegration.core.mail.SendMail;
import com.apiintegration.core.model.Token;
import com.apiintegration.core.model.User;

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

			Map<String, String> model = new HashMap();
			model.put("logoUrl", appLogo);
			model.put("name", user.getUserFullName().toUpperCase());
			model.put("code", token.getToken());

			mail.setModel(model);

			System.out.println("2FA code: {}" + token.getToken());
			mailSender.sendSimpleMessage(mail);

		} catch (Exception e) {
			log.error("Error processing SendTwoFactorEmailEvent!", e);
		}
	}

	public void sendVerifyEmailMail(User user , Token token) {
		try {
			Mail mail = new Mail();
			mail.setFrom(from);
			mail.setFromPretty(fromPretty);
			mail.setTo(user.getUserEmail());
			mail.setSubject("Verify Your Email On APIIntegration");
			mail.setContent("verify_email.ftl");

			Map<String, String> model = new HashMap();
			model.put("logoUrl", appLogo);
			model.put("name", user.getUserFullName().toUpperCase());
			model.put("url", appUrl + "/verify-email/?token=" + token.getToken());

			mail.setModel(model);

			mailSender.sendSimpleMessage(mail);

		} catch (Exception e) {
			log.error("Error processing SendTwoFactorEmailEvent!", e);
		}

	}

	public void sendAccountInviteMail(String toEmail ,User user ,Token token) {
		try {
			Mail mail = new Mail();
			mail.setFrom(from);
			mail.setFromPretty(fromPretty);
			mail.setTo(toEmail);
			mail.setSubject("Account Invite from APIIntegration");
			mail.setContent("account_invite.ftl");

			Map<String, String> model = new HashMap();
			model.put("logoUrl", appLogo);
			model.put("name", user.getUserEmail());
			model.put("url", appUrl + "/account/join?token=" + token.getToken());

			mail.setModel(model);

			mailSender.sendSimpleMessage(mail);

		} catch (Exception e) {
			log.error("Error processing SendTwoFactorEmailEvent!", e);
		}

	}
}
