package com.example.commonlib.security.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import reactor.core.publisher.Mono;

@AutoConfiguration
@Slf4j
public class JwtAuthoritiesAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "jwtAuthConverterServlet")
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthConverterServlet(SecuritySharedProperties props) {

        JwtGrantedAuthoritiesConverter gac = new JwtGrantedAuthoritiesConverter();
        gac.setAuthoritiesClaimName(props.getRolesClaim());
        gac.setAuthorityPrefix(props.getRolePrefix());

        JwtAuthenticationConverter jac = new JwtAuthenticationConverter();
        jac.setJwtGrantedAuthoritiesConverter(gac);

        return jac; // JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken>
    }

    @Bean
    @ConditionalOnMissingBean(name = "jwtAuthConverterReactive")
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthConverterReactive(
            Converter<Jwt, AbstractAuthenticationToken> jwtAuthConverterServlet
    ) {
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthConverterServlet);
    }


}
