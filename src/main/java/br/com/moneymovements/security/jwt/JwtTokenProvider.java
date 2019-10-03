package br.com.moneymovements.security.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import br.com.moneymovements.exception.InvalidJWTAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtTokenProvider {

	@Value("${security.jwt.token.secret-key:secret}")
	private String secretkey = "secret";
	
	@Value("${security.jwt.token.expire-lenght:3600000}")
	private long validityInMilliseconds = 3600000;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@PostConstruct
	private void init() {
		secretkey = Base64.getEncoder().encodeToString(secretkey.getBytes());
	}
	
	public String createToken (String userName, List<String> roles) {
		
		Claims claims = Jwts.claims().setSubject(userName);
		claims.put("roles", roles);
		
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);
		
		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(validity)
				.signWith(SignatureAlgorithm.HS256, secretkey)
				.compact();
	}
	
	public String resolveToken (HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}
	
	public Boolean validateToken (String token) throws InvalidJWTAuthenticationException {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretkey).parseClaimsJws(token);
			if (claims.getBody().getExpiration().before(new Date())) {
				return false;
			}
			return true;
		} catch (Exception e) {
			throw new InvalidJWTAuthenticationException("Token expired or not found");
		}
	}
	
	public Authentication getAuthentication (String token) {
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	private String getUsername(String token) {
		return Jwts.parser().setSigningKey(secretkey).parseClaimsJws(token).getBody().getSubject();
	}
	
}
