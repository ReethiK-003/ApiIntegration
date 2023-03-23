package com.apiintegration.core.mail;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mail {
	private String from;
	private String fromPretty;
	private String to;
	private String cc;
	private String bcc;
	private String replyTo;
	private String subject;
	private String content;
	private Map model;
	private List<MailAttachment> attachments;

	public Mail(String from, String to, String subject, String content) {
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.content = content;
		this.attachments = new LinkedList<>();
	}
}