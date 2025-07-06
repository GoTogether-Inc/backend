package com.gotogether.global.oauth.exception;

import org.springframework.security.core.AuthenticationException;

public class DuplicatedEmailException extends AuthenticationException {
	public DuplicatedEmailException(String msg) {
		super(msg);
	}
}