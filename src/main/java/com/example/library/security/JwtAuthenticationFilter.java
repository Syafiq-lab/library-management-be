package com.example.library.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter to authenticate requests based on JWT tokens.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	/**
	 * Filters incoming requests and sets authentication if a valid JWT is found.
	 *
	 * @param request     HttpServletRequest
	 * @param response    HttpServletResponse
	 * @param filterChain FilterChain
	 * @throws ServletException if an error occurs during the filter process
	 * @throws IOException      if an input or output error occurs
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain)
			throws ServletException, IOException {

		try {
			String jwt = getJwtFromRequest(request);

			if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
				String username = tokenProvider.getUsernameFromJWT(jwt);

				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());

				authentication.setDetails(
						new WebAuthenticationDetailsSource().buildDetails(request));

				// Set the authentication in the SecurityContext
				SecurityContextHolder.getContext().setAuthentication(authentication);
				logger.info("Authentication set for user: {}", username);
			} else {
				logger.warn("JWT is missing or invalid");
			}

			filterChain.doFilter(request, response);
		} catch (Exception ex) {
			// Log the exception to verify if it's being swallowed
			logger.error("Exception in JwtAuthenticationFilter: ", ex);
			throw ex;  // Ensure that the exception is propagated further
		}
	}

	/**
	 * Extracts the JWT token from the Authorization header.
	 *
	 * @param request HttpServletRequest
	 * @return JWT token as a String
	 */
	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		final String TOKEN_PREFIX = "Bearer ";
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
			return bearerToken.substring(TOKEN_PREFIX.length());
		}
		return null;
	}
}
