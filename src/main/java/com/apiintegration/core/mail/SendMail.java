package com.apiintegration.core.mail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.apiintegration.core.model.service.FileStorageService;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SendMail {

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private Configuration freemarkerConfig;

	@Autowired
	private FileStorageService fileStorageService;

	public void sendSimpleMessage(Mail mail) throws MessagingException, IOException, TemplateException {

		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
				StandardCharsets.UTF_8.name());

		Template t = freemarkerConfig.getTemplate(mail.getContent());
		String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, mail.getModel());

		helper.setTo(mail.getTo());
		helper.setText(html, true);
		helper.setSubject(mail.getSubject());
		if (mail.getFromPretty() != null) {
			helper.setFrom(mail.getFrom(), mail.getFromPretty());
		} else {
			helper.setFrom(mail.getFrom());
		}

		if (mail.getBcc() != null)
			helper.setBcc(mail.getBcc());
		if (mail.getCc() != null)
			helper.setCc(mail.getCc());
		if (mail.getReplyTo() != null)
			helper.setReplyTo(mail.getReplyTo());
		if (mail.getAttachments() != null) {
			mail.getAttachments().forEach(attachment -> {
				try {
					helper.addAttachment(attachment.getOriginalFilename(),
							fileStorageService.loadFileAsResource(attachment.getStoredFilename()));
				} catch (MessagingException e) {
					log.error("Error adding attachment to email!", e);
				}
			});
		}

		emailSender.send(message);
	}
}