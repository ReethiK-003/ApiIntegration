package com.apiintegration.core.utils;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.apiintegration.core.model.Token;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@DiscriminatorValue(TokenTypes.CONFIRM_EMAIL)
public class ConfirmEmailToken extends Token {
	public static final int EXPIRATION = 60 * 24 * 30;

	public ConfirmEmailToken() {
		super.setType(TokenTypes.CONFIRM_EMAIL);
	}
}