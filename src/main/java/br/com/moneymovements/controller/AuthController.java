package br.com.moneymovements.controller;

import static org.springframework.http.ResponseEntity.ok;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.moneymovements.domain.User;
import br.com.moneymovements.exception.UnableToCreateUserException;
import br.com.moneymovements.repository.UserRepository;
import br.com.moneymovements.security.AccountCredentialsVO;
import br.com.moneymovements.security.jwt.JwtTokenProvider;
import br.com.moneymovements.service.UserService;

@CrossOrigin()
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/signup")
	public ResponseEntity signup(@RequestBody User user) throws UnableToCreateUserException {
		
		User newUser = userService.createUser(user);
		
		return ok(HttpServletResponse.SC_CREATED);
		
	}

	@PostMapping("/signin")
	public ResponseEntity signin(@RequestBody AccountCredentialsVO data) {
		
		try {
			
			String username = data.getUsername();
			String password = data.getPassword();
			User user = userRepository.findByUserName(username);
			String token;
			
			if (user != null) {
				token = jwtTokenProvider.createToken(username, user.getRoles());
			} else {
				throw new UsernameNotFoundException("Username not found.");
			}
			
			Map<Object, Object> model = new HashMap<>();
			model.put("username", username);
			model.put("token", token);
			
			return ok(model);
			
		} catch (AuthenticationException e) {
			throw new BadCredentialsException("Username or password invalid");
		}
		
	}
	
}
