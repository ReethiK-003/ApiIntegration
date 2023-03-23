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
@DiscriminatorValue(TokenTypes.ACCOUNT_INVITE)
public class AccountInviteToken extends Token {

	public static final int EXPIRATION = 60 * 24 * 30;

	public AccountInviteToken() {
		super.setType(TokenTypes.ACCOUNT_INVITE);
	}

}