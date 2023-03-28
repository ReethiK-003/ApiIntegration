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
@DiscriminatorValue(TokenTypes.DELETE_ACCOUNT)
public class DeleteAccountToken extends Token {

	public static final int EXPIRATION = 60 * 24;

	public DeleteAccountToken() {
		super.setType(TokenTypes.DELETE_ACCOUNT);
	}

}
