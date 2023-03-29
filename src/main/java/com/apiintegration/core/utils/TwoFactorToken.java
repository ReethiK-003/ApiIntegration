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
@DiscriminatorValue(TokenTypes.TWO_FACTOR)
public class TwoFactorToken extends Token {

	public static final int EXPIRATION = 10; // 10 minutes

	public TwoFactorToken() {
		super.setType(TokenTypes.TWO_FACTOR);
	}

}