package com.mb.transactionbackend.service.impl;

import com.mb.transactionbackend.dto.BorrowerRegistrationRequest;
import com.mb.transactionbackend.exception.ResourceNotFoundException;
import com.mb.transactionbackend.mapper.BorrowerMapper;
import com.mb.transactionbackend.model.Borrower;
import com.mb.transactionbackend.repository.BorrowerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowerServiceImplTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private BorrowerMapper borrowerMapper;

    @InjectMocks
    private BorrowerServiceImpl borrowerService;

    private BorrowerRegistrationRequest request;
    private Borrower sampleBorrower;

    @BeforeEach
    void setUp() {
        sampleBorrower = Borrower.builder()
                .id(1L)
                .borrowerId("BOR001")
                .name("Alice Smith")
                .email("alice@example.com")
                .build();

        request = new BorrowerRegistrationRequest(
                "BOR001",
                "Alice Smith",
                "alice@example.com"
        );
    }

    @Test
    @DisplayName("registerBorrower: success when ID and email are new")
    void registerBorrower_Success() {
        when(borrowerRepository.existsByBorrowerId("BOR001")).thenReturn(false);
        when(borrowerRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(borrowerMapper.toEntity(request)).thenReturn(sampleBorrower);
        when(borrowerRepository.save(sampleBorrower)).thenReturn(sampleBorrower);

        Borrower result = borrowerService.registerBorrower(request);

        assertSame(sampleBorrower, result, "Should return the saved borrower");
        verify(borrowerRepository, times(1)).save(sampleBorrower); // verify save called once :contentReference[oaicite:5]{index=5}
    }

    @Test
    @DisplayName("registerBorrower: fail on duplicate ID")
    void registerBorrower_DuplicateId() {
        when(borrowerRepository.existsByBorrowerId("BOR001")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> borrowerService.registerBorrower(request)
        );
        assertEquals("Borrower ID already exists", ex.getMessage());
        verify(borrowerRepository, never()).save(any());
    }

    @Test
    @DisplayName("registerBorrower: fail on duplicate email")
    void registerBorrower_DuplicateEmail() {
        when(borrowerRepository.existsByBorrowerId("BOR001")).thenReturn(false);
        when(borrowerRepository.existsByEmail("alice@example.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> borrowerService.registerBorrower(request)
        );
        assertEquals("Email already exists", ex.getMessage());
        verify(borrowerRepository, never()).save(any());
    }

    @Test
    @DisplayName("findByBorrowerId: success when borrower exists")
    void findByBorrowerId_Success() {
        when(borrowerRepository.findByBorrowerId("BOR001"))
                .thenReturn(Optional.of(sampleBorrower));

        Borrower found = borrowerService.findByBorrowerId("BOR001");
        assertSame(sampleBorrower, found);
    }

    @Test
    @DisplayName("findByBorrowerId: not found throws ResourceNotFoundException")
    void findByBorrowerId_NotFound() {
        when(borrowerRepository.findByBorrowerId("BOR999"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> borrowerService.findByBorrowerId("BOR999"),
                "Should throw when borrowerId does not exist"
        );
    }
}
