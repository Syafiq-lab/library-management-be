package com.mb.transactionbackend.service.impl;

import com.mb.transactionbackend.dto.UserRegistrationRequest;
import com.mb.transactionbackend.exception.DuplicateResourceException;
import com.mb.transactionbackend.exception.ResourceNotFoundException;
import com.mb.transactionbackend.mapper.UserMapper;
import com.mb.transactionbackend.model.User;
import com.mb.transactionbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserRegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("encoded_password")
                .createdAt(Instant.now())
                .deleted(false)
                .build();
        
        registrationRequest = new UserRegistrationRequest();
        // Set necessary fields for registrationRequest
    }

    @Test
    void registerUser_WhenUsernameNotInUse_ShouldRegisterAndReturnUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userMapper.toEntity(any(UserRegistrationRequest.class), any(PasswordEncoder.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.registerUser(registrationRequest);

        assertEquals(testUser, result);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void registerUser_WhenUsernameInUse_ShouldThrowException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        assertThrows(DuplicateResourceException.class, () -> userService.registerUser(registrationRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.findById(1L);

        assertEquals(testUser, result);
    }

    @Test
    void findById_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(99L));
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User result = userService.findByUsername("testuser");

        assertEquals(testUser, result);
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findByUsername("nonexistent"));
    }

    @Test
    void findAllUsers_ShouldReturnAllUsers() {
        List<User> users = Arrays.asList(
                testUser,
                User.builder()
                        .id(2L)
                        .username("anotheruser")
                        .password("another_password")
                        .createdAt(Instant.now())
                        .deleted(false)
                        .build()
        );

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAllUsers();

        assertEquals(2, result.size());
        assertEquals(users, result);
    }
    
    @Test
    void deleteUser_WhenUserExists_ShouldMarkAsDeleted() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.deleteUser(1L);

        assertTrue(testUser.isDeleted());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(99L));
        verify(userRepository, never()).save(any(User.class));
    }
}