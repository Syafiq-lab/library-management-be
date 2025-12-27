package com.example.authservice.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secretKey;

    @Value("${security.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${security.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(UserDetails userDetails) {
        log.debug("Generating access token for user={}", userDetails.getUsername());
        return generateToken(new HashMap<>(), userDetails, jwtExpirationMs);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        log.debug("Generating refresh token for user={}", userDetails.getUsername());
        Map<String, Object> claims = new HashMap<>();
        claims.put("typ", "refresh");
        return generateToken(claims, userDetails, refreshExpirationMs);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        // Do not log token contents
        final String username = extractUsername(token);
        boolean matches = username.equals(userDetails.getUsername());
        boolean expired = isTokenExpired(token);
        boolean valid = matches && !expired;
        if (!valid && log.isDebugEnabled()) {
            log.debug("Token invalid for user={} matchesUsername={} expired={}", userDetails.getUsername(), matches, expired);
        }
        return valid;
    }

    public boolean isRefreshToken(String token) {
        Claims claims = extractAllClaims(token);
        Object typ = claims.get("typ");
        return "refresh".equals(typ);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expiration)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        // Allow either plain text or base64 encoded secret
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secretKey);
            if (log.isDebugEnabled()) {
                log.debug("JWT secret interpreted as base64");
            }
        } catch (IllegalArgumentException ex) {
            keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            if (log.isDebugEnabled()) {
                log.debug("JWT secret interpreted as plain text");
            }
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
