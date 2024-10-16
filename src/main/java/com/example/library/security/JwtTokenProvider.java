package com.example.library.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for generating and validating JWT tokens.
 */
@Component
@Slf4j
public class JwtTokenProvider {

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	@Value("${app.jwtExpirationMs}")
	private int jwtExpirationMs;

	/**
	 * Generates a JWT token for the given username.
	 *
	 * @param username Username
	 * @return JWT token as a String
	 */
	public String generateToken(String username) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	/**
	 * Extracts username from the JWT token.
	 *
	 * @param token JWT token
	 * @return Username as a String
	 */
	public String getUsernameFromJWT(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(jwtSecret)
				.parseClaimsJws(token)
				.getBody();

		return claims.getSubject();
	}

	/**
	 * Validates the JWT token.
	 *
	 * @param token JWT token
	 * @return true if valid, false otherwise
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
			return true;
		} catch (JwtException ex) {
			log.error("Invalid JWT token", ex);
		}
		return false;
	}
}
