package com.mb.transactionbackend.service.impl;

import com.mb.transactionbackend.auth.JwtService;
import com.mb.transactionbackend.dto.AuthRequest;
import com.mb.transactionbackend.dto.UserRegistrationRequest;
import com.mb.transactionbackend.exception.DuplicateResourceException;
import com.mb.transactionbackend.exception.ResourceNotFoundException;
import com.mb.transactionbackend.exception.UnauthorizedException;
import com.mb.transactionbackend.mapper.UserMapper;
import com.mb.transactionbackend.model.User;
import com.mb.transactionbackend.repository.UserRepository;
import com.mb.transactionbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    

    @Override
    @CacheEvict(value = {"users", "usersList"}, allEntries = true)
    public User registerUser(UserRegistrationRequest request) {
        log.info("Processing user registration request for username: {}", request.getUsername());
        
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            log.warn("Registration failed: Username '{}' already taken", request.getUsername());
            throw new DuplicateResourceException("Username already taken");
        }

        User user = userMapper.toEntity(request, passwordEncoder);
        User savedUser = userRepository.save(user);
        
        log.info("User registered successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    public String authenticateUser(AuthRequest request) {
        log.info("Processing authentication request for username: {}", request.getUsername());
        
        try {
            /* ---- authenticate with Spring Security ---- */
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtService.generateToken(authentication);
            log.info("Authentication successful for user: {}", request.getUsername());
            return token;
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", request.getUsername(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        log.debug("Retrieving current authenticated user");
        
        String username =
                Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                        .map(Authentication::getName)
                        .orElseThrow(() -> {
                            log.error("No authenticated user found in security context");
                            return new UnauthorizedException("No authenticated user");
                        });

        log.debug("Found username in security context: {}", username);
        
        return (User) userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> {
                    log.error("User not found in database for username: {}", username);
                    return new ResourceNotFoundException("User not found");
                });
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Processing request to delete user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Cannot delete: User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        user.setDeleted(true);
        userRepository.save(user);
        log.info("User with ID: {} has been soft-deleted", userId);
    }
}