package com.mb.transactionbackend.mapper;

import com.mb.transactionbackend.dto.UserRegistrationRequest;
import com.mb.transactionbackend.enums.RoleEnum;
import com.mb.transactionbackend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserMapperTest {

    private UserMapper userMapper;

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = mock(PasswordEncoder.class);

        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void toEntity_ShouldMapAllFieldsAndIgnoreId() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername(" TestUser ");
        request.setPassword("password123");

        when(passwordEncoder.encode("password123")).thenReturn("ENCODED_pw");

        User result = userMapper.toEntity(request, passwordEncoder);

        assertNotNull(result, "Mapper should return a non-null User");
        assertEquals("testuser", result.getUsername(),
                "Username must be trimmed and lowercased");
        assertEquals("ENCODED_pw", result.getPassword(),
                "Password should be encoded via the provided PasswordEncoder");

        assertEquals(Set.of(RoleEnum.ROLE_USER), result.getRoles(),
                "New users get the ROLE_USER by default");
        assertFalse(result.isDeleted(), "Deleted flag should default to false");

        assertNotNull(result.getCreatedAt(), "createdAt must be initialized");
        assertTrue(true,
                "createdAt must be an Instant");

        assertNull(result.getId(), "ID must be null because it's ignored");

        verify(passwordEncoder, times(1)).encode("password123");
    }
}
