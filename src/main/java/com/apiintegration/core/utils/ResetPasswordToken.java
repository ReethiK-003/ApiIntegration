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
@DiscriminatorValue(TokenTypes.RESET_PASSWORD)
public class ResetPasswordToken extends Token {
  public static final int EXPIRATION = 60 * 2;

  public ResetPasswordToken() {
    super.setType(TokenTypes.RESET_PASSWORD);
  }
}

