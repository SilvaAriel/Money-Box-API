package br.com.moneymovements.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.moneymovements.domain.Permission;
import br.com.moneymovements.domain.User;
import br.com.moneymovements.exception.UnableToCreateUserException;
import br.com.moneymovements.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		User user = userRepository.findByUserName(userName);
		if (user != null) {
			return user;
		} else {
			throw new UsernameNotFoundException("Username " + userName + " not found.");
		}
	}

	@Override
	public User createUser(User user) throws UnableToCreateUserException {

		try {
			user.setPassword(encryptPassword(user.getPassword()));
			user.setAccountNonExpired(true);
			user.setAccountNonLocked(true);
			user.setCredentialsNonExpired(true);
			user.setIsEnabled(true);
			
			List<Permission> permissions = new ArrayList<>();
			Permission permission = new Permission();
			permission.setId(3L);
			permission.setDescription("COMMON_USER");
			permissions.add(permission);
			user.setPermissions(permissions);
			
			userRepository.save(user);
			
			return user;
		} catch (Exception e) {
			throw new UnableToCreateUserException("Unable to create user");
		}
		
	}

	private String encryptPassword(String password) {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder.encode(password);
	}
	
	public User getCurrentUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

}
