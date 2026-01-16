package com.example.authservice.auth;

import com.example.authservice.client.UserClient;
import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.security.jwt.JwtService;
import com.example.authservice.token.RefreshToken;
import com.example.authservice.token.RefreshTokenRepository;
import com.example.authservice.user.Role;
import com.example.authservice.user.User;
import com.example.authservice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserClient userClient;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.debug("Register attempt via user-service: email={}", request.getEmail());
        
        // 1. Delegate user creation to the owner of the data
        userClient.createUser(request);

        // 2. Fetch the newly created user to generate tokens
        // (Alternatively, have UserClient return the user object)
        User user = userRepository.findByEmail(request.getEmail().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new RuntimeException("User creation failed or sync delay"));

        log.info("User registered via delegation email={} id={}", user.getEmail(), user.getId());

        UserDetails userDetails = buildUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = createAndStoreRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }
    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication;
try {
    log.debug("Authentication attempt email={}", request.getEmail());
    authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            )
    );
} catch (AuthenticationException ex) {
    log.warn("Authentication failed email={} reason={}", request.getEmail(), ex.getMessage());
    throw ex;
}
if (!authentication.isAuthenticated()) {
            log.warn("Authentication rejected: invalid credentials email={}", request.getEmail());
            throw new IllegalArgumentException("Invalid credentials");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.info("Authentication success email={} userId={} role={}", user.getEmail(), user.getId(), user.getRole());

        UserDetails userDetails = buildUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = createAndStoreRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse refresh(String refreshTokenValue) {
        log.debug("Refresh attempt tokenPresent={} tokenLength={}", refreshTokenValue != null, refreshTokenValue == null ? 0 : refreshTokenValue.length());
        if (!jwtService.isRefreshToken(refreshTokenValue)) {
            log.warn("Refresh rejected: not a refresh token");
            throw new IllegalArgumentException("Invalid token type");
        }

        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenAndRevokedFalse(refreshTokenValue)
                .orElseThrow(() -> {
                    log.warn("Refresh rejected: token not found or revoked");
                    return new IllegalArgumentException("Refresh token not found or revoked");
                });

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            log.warn("Refresh rejected: token expired userId={}", refreshToken.getUser() == null ? null : refreshToken.getUser().getId());
            throw new IllegalArgumentException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        UserDetails userDetails = buildUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails);

        // Rotate refresh token
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        String newRefreshTokenValue = createAndStoreRefreshToken(user);

        log.info("Refresh success email={} userId={}", user.getEmail(), user.getId());
        return new AuthResponse(accessToken, newRefreshTokenValue);
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        log.debug("Logout attempt tokenPresent={} tokenLength={}", refreshTokenValue != null, refreshTokenValue == null ? 0 : refreshTokenValue.length());
        refreshTokenRepository.findByTokenAndRevokedFalse(refreshTokenValue)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    log.info("Logout success: refresh token revoked userId={}", token.getUser() == null ? null : token.getUser().getId());
                    refreshTokenRepository.save(token);
                });
    }

    private String createAndStoreRefreshToken(User user) {
        Instant expiry = Instant.now().plus(7, ChronoUnit.DAYS);
        UserDetails userDetails = buildUserDetails(user);
        String rawToken = jwtService.generateRefreshToken(userDetails);

        log.debug("Issued refresh token userId={} expiresAt={}", user.getId(), expiry);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(rawToken)
                .user(user)
                .expiryDate(expiry)
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .disabled(!user.isActive())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
}