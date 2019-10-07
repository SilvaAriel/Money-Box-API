package br.com.moneymovements.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.com.moneymovements.domain.User;
import br.com.moneymovements.exception.UnableToCreateUserException;

public interface UserService {

	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException;
	
	public UserDetails loadUserByUsernameAndPassword(String username, String password) throws UsernameNotFoundException;
	
	public User createUser(User user) throws UnableToCreateUserException;
	
	public User getCurrentUser();
	
}
