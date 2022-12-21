package com.flab.sooldama.domain.product.exception;

public class AuthenticationFailException extends RuntimeException {

	public AuthenticationFailException(String message) {
		super(message);
	}
}
