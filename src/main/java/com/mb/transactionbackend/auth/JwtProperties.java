package com.mb.transactionbackend.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    private String secret;
    private long expirationMs = 3600000; // 1 hour default
    private String issuer = "library-management-system";
}