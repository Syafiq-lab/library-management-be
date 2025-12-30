package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

		http
				.csrf(csrf -> csrf.disable())

				.authorizeExchange(authorize -> authorize
						// allow authorization API public
						.pathMatchers("/api/auth/**").permitAll()

						// swagger UI + assets
						.pathMatchers("/swagger-ui/**").permitAll()
						.pathMatchers("/v3/api-docs/**").permitAll()
						.pathMatchers("/webjars/**").permitAll()
						.pathMatchers("/favicon.ico").permitAll()
						.pathMatchers("/swagger-ui/index.html").permitAll()

						// health, actuator if needed
						.pathMatchers("/actuator/health").permitAll()

						// everything else -> auth
						.anyExchange().authenticated()
				);

		return http.build();
	}
}
