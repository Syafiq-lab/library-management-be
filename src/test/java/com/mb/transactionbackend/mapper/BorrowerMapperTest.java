package com.mb.transactionbackend.mapper;

import com.mb.transactionbackend.dto.BorrowerRegistrationRequest;
import com.mb.transactionbackend.model.Borrower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class BorrowerMapperTest {

    private BorrowerMapper borrowerMapper;

    @BeforeEach
    void setUp() {
        borrowerMapper = Mappers.getMapper(BorrowerMapper.class);
    }

    @Test
    void toEntity_ShouldMapAllFieldsAndIgnoreId() {
        // Given: a record instance (using its canonical constructor)
        BorrowerRegistrationRequest request = new BorrowerRegistrationRequest(
                "BOR001",
                "John Doe",
                "john.doe@example.com"
        );

        Borrower result = borrowerMapper.toEntity(request);

        assertNotNull(result);
        assertEquals(request.borrowerId(), result.getBorrowerId());
        assertEquals(request.name(),       result.getName());
        assertEquals(request.email(),      result.getEmail());

        assertNull(result.getId());
    }
}
