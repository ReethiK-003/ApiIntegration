package com.apiintegration.core.mail;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailAttachment {
	private String storedFilename;
	private String originalFilename;
}