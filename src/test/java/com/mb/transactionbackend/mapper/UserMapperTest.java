package com.mb.transactionbackend.mapper;

import com.mb.transactionbackend.dto.UserRegistrationRequest;
import com.mb.transactionbackend.enums.RoleEnum;
import com.mb.transactionbackend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
    }

    @Test
    void toEntity_ShouldMapCorrectly() {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername(" TestUser ");
        request.setPassword("password123");
        
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");

        // When
        User result = userMapper.toEntity(request, passwordEncoder);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encoded_password", result.getPassword());
        assertFalse(result.isDeleted());
        assertNotNull(result.getCreatedAt());
        assertEquals(Set.of(RoleEnum.ROLE_USER), result.getRoles());
        verify(passwordEncoder, times(1)).encode("password123");
    }
}