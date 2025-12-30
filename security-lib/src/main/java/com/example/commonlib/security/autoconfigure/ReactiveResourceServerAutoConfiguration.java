package com.example.commonlib.security.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Slf4j
public class ReactiveResourceServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SecurityWebFilterChain.class)
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            SecuritySharedProperties props,
            Converter<Jwt, Mono<? extends AbstractAuthenticationToken>> jwtAuthConverterReactive
    ) {
        log.info("Building SecurityWebFilterChain (REACTIVE, stateless JWT)");

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .authorizeExchange(ex -> {
                    ex.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    for (String p : props.getPermitAll()) {
                        ex.pathMatchers(p).permitAll();
                    }
                    ex.anyExchange().authenticated();
                })
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverterReactive))
                )
                .build();
    }
}
