package br.com.moneymovements.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import br.com.moneymovements.exception.InvalidJWTAuthenticationException;

public class JwtTokenFilter extends GenericFilterBean{

	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String token = this.jwtTokenProvider.resolveToken((HttpServletRequest) request);
		
		try {
			if (token != null && this.jwtTokenProvider.validateToken(token)) {
				
				Authentication auth = this.jwtTokenProvider.getAuthentication(token);
				
				if (auth != null) {
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}
		} catch (InvalidJWTAuthenticationException e) {
			HttpServletResponse errorResponse = (HttpServletResponse) response;
			errorResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token expired or not found");
		}
		
		chain.doFilter(request, response);
		
	}

	
	
}
