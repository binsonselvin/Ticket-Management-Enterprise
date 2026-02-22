package com.sk.workitem.app.errors;

import org.springframework.security.core.AuthenticationException;

public class AccountLockedException extends AuthenticationException  {
	public AccountLockedException(String errorMssg) {
		super(errorMssg);
	}
}
