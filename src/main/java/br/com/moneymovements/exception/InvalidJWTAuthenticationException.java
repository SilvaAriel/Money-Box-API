package br.com.moneymovements.exception;

import javax.naming.AuthenticationException;

public class InvalidJWTAuthenticationException extends AuthenticationException {
	private static final long serialVersionUID = 1L;
	
	public InvalidJWTAuthenticationException (String message) {
		super(message);
	}

}
