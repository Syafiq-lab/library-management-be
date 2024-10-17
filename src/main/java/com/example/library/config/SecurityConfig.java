package com.example.library.config;

import com.example.library.security.*;
import com.example.library.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for Spring Security.
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Enable method-level security
public class SecurityConfig {

	private final JwtAuthenticationEntryPoint unauthorizedHandler;
	private final CustomAccessDeniedHandler accessDeniedHandler; // Inject custom AccessDeniedHandler
	private final CustomUserDetailsService userDetailsService;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	/**
	 * Bean for password encoding using BCrypt.
	 *
	 * @return PasswordEncoder instance
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(Constants.BCRYPT_STRENGTH);
	}

	/**
	 * Bean for authentication manager.
	 *
	 * @param http HttpSecurity instance
	 * @return AuthenticationManager instance
	 * @throws Exception if an error occurs
	 */
	@Bean
	public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
		auth.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder());
		return auth.build();
	}

	/**
	 * Configures HTTP security, including CSRF, session management, and endpoint authorization.
	 *
	 * @param http HttpSecurity instance
	 * @return SecurityFilterChain instance
	 * @throws Exception if an error occurs
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()
				.exceptionHandling()
				.authenticationEntryPoint(unauthorizedHandler) // Handle unauthorized access
				.accessDeniedHandler(accessDeniedHandler) // Handle access denied
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Use stateless session management
				.and()
				.authorizeRequests()
				.antMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Public endpoints
				.antMatchers(HttpMethod.GET, "/api/books/**").permitAll() // Allow GET requests to /api/books/**
				.anyRequest().authenticated(); // All other endpoints require authentication

		// Add JWT authentication filter
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
