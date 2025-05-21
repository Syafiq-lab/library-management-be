package com.mb.transactionbackend.service.impl;

import com.mb.transactionbackend.auth.JwtService;
import com.mb.transactionbackend.dto.AuthRequest;
import com.mb.transactionbackend.dto.UserRegistrationRequest;
import com.mb.transactionbackend.enums.RoleEnum;
import com.mb.transactionbackend.exception.DuplicateResourceException;
import com.mb.transactionbackend.exception.ResourceNotFoundException;
import com.mb.transactionbackend.exception.UnauthorizedException;
import com.mb.transactionbackend.mapper.UserMapper;
import com.mb.transactionbackend.model.User;
import com.mb.transactionbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private UserMapper userMapper;
    @InjectMocks private UserServiceImpl userService;

    private User sampleUser;
    private UserRegistrationRequest regRequest;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("encoded_pw")
                .roles(Set.of(RoleEnum.ROLE_USER))
                .createdAt(Instant.now())
                .deleted(false)
                .build();

        regRequest = new UserRegistrationRequest();
        regRequest.setUsername("TestUser");
        regRequest.setPassword("rawpassword");
    }

    @Test
    @DisplayName("registerUser: success when username is new")
    void registerUser_Success() {
        when(userRepository.existsByUsernameIgnoreCase("TestUser")).thenReturn(false);
        when(userMapper.toEntity(eq(regRequest), any(PasswordEncoder.class))).thenReturn(sampleUser);
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        User result = userService.registerUser(regRequest);

        assertSame(sampleUser, result);
        verify(userRepository).save(sampleUser);
    }

    @Test
    @DisplayName("registerUser: throws on duplicate username")
    void registerUser_Duplicate() {
        when(userRepository.existsByUsernameIgnoreCase("TestUser")).thenReturn(true);

        DuplicateResourceException ex = assertThrows(
                DuplicateResourceException.class,
                () -> userService.registerUser(regRequest)
        );
        assertEquals("Username already taken", ex.getMessage());
        verify(userRepository, never()).save(any());
    }


    @Test
    @DisplayName("authenticateUser: success returns token")
    void authenticateUser_Success() {
        AuthRequest auth = new AuthRequest();
        auth.setUsername("testuser");
        auth.setPassword("rawpassword");
        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(jwtService.generateToken(mockAuth)).thenReturn("jwt-token");

        String token = userService.authenticateUser(auth);

        assertEquals("jwt-token", token);
        verify(jwtService).generateToken(mockAuth);
    }

    @Test
    @DisplayName("getCurrentUser: returns user from security context")
    void getCurrentUser_Success() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userRepository.findByUsernameIgnoreCase("testuser"))
                .thenReturn(Optional.of(sampleUser));

        User current = userService.getCurrentUser();

        assertSame(sampleUser, current);
    }

    @Test
    @DisplayName("getCurrentUser: throws if no auth")
    void getCurrentUser_NoAuth() {
        SecurityContextHolder.clearContext();
        assertThrows(UnauthorizedException.class, () -> userService.getCurrentUser());
    }

    @Test
    @DisplayName("deleteUser: soft-deletes existing user")
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        userService.deleteUser(1L);

        assertTrue(sampleUser.isDeleted());
        verify(userRepository).save(sampleUser);
    }

    @Test
    @DisplayName("deleteUser: throws if not found")
    void deleteUser_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(99L));
        verify(userRepository, never()).save(any());
    }

}