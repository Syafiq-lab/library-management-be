package com.mb.transactionbackend.mapper;

import com.mb.transactionbackend.dto.BorrowerRegistrationRequest;
import com.mb.transactionbackend.model.Borrower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BorrowerMapperTest {

    private BorrowerMapper borrowerMapper;

    @BeforeEach
    void setUp() {
        borrowerMapper = new BorrowerMapperImpl();
    }

    @Test
    void toEntity_ShouldMapCorrectly() {
        // Given
        BorrowerRegistrationRequest request = new BorrowerRegistrationRequest();
        request.setBorrowerId("BOR001");
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");

        // When
        Borrower result = borrowerMapper.toEntity(request);

        // Then
        assertNotNull(result);
        assertEquals("BOR001", result.getBorrowerId());
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertNull(result.getId());
    }
}