package com.example.authservice.client;

import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.security.jwt.JwtService;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", path = "/api/users", configuration = UserClient.Configuration.class)
public interface UserClient {

    @PostMapping
    void createUser(@RequestBody RegisterRequest request);

    @GetMapping("/{id}")
    Object getUserById(@PathVariable("id") Long id);

    @Slf4j
    class Configuration {

        @Bean
        public RequestInterceptor internalAuthInterceptor(JwtService jwtService) {
            return requestTemplate -> {
                log.debug("Generating internal system token for service-to-service call");

                // Create a system user representation for the token
                User systemUser = new User(
                        "auth-service-internal",
                        "",
                        List.of(new SimpleGrantedAuthority("ROLE_INTERNAL"))
                );

                String token = jwtService.generateAccessToken(systemUser);
                requestTemplate.header("Authorization", "Bearer " + token);
            };
        }
    }
}
