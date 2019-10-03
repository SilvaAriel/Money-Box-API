package br.com.moneymovements.service;

import br.com.moneymovements.domain.User;
import br.com.moneymovements.exception.UnableToCreateUserException;

public interface UserService  {

	public User createUser(User user) throws UnableToCreateUserException;
	
}
