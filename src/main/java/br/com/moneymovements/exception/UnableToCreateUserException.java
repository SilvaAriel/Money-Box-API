package br.com.moneymovements.exception;

public class UnableToCreateUserException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public UnableToCreateUserException (String message) {
		super(message);
	}

}
