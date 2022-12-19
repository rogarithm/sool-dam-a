package com.flab.sooldama.domain.user.exception;

public class UserAlreadyLoggedinException extends RuntimeException {

	public UserAlreadyLoggedinException(String message) {
		super(message);
	}
}
