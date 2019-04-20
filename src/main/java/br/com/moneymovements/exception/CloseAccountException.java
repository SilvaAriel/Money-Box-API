package br.com.moneymovements.exception;

public class CloseAccountException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public CloseAccountException (String name) {
		super(name);
	}

}
