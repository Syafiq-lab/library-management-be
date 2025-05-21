package com.mb.transactionbackend.service.impl;

import com.mb.transactionbackend.dto.BorrowerRegistrationRequest;
import com.mb.transactionbackend.exception.DuplicateResourceException;
import com.mb.transactionbackend.exception.ResourceNotFoundException;
import com.mb.transactionbackend.mapper.BorrowerMapper;
import com.mb.transactionbackend.model.Borrower;
import com.mb.transactionbackend.repository.BorrowerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowerServiceImplTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private BorrowerMapper borrowerMapper;

    @InjectMocks
    private BorrowerServiceImpl borrowerService;

    private Borrower testBorrower;
    private BorrowerRegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        testBorrower = Borrower.builder()
                .id(1L)
                .borrowerId("BOR001")
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        
        registrationRequest = new BorrowerRegistrationRequest();
        // Set necessary fields for registrationRequest
    }

    @Test
    void registerBorrower_WhenEmailNotInUse_ShouldRegisterAndReturnBorrower() {
        when(borrowerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(borrowerMapper.toEntity(any(BorrowerRegistrationRequest.class))).thenReturn(testBorrower);
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(testBorrower);

        Borrower result = borrowerService.registerBorrower(registrationRequest);

        assertEquals(testBorrower, result);
        verify(borrowerRepository, times(1)).save(testBorrower);
    }

    @Test
    void registerBorrower_WhenEmailInUse_ShouldThrowException() {
        when(borrowerRepository.findByEmail(anyString())).thenReturn(Optional.of(testBorrower));

        assertThrows(DuplicateResourceException.class, () -> borrowerService.registerBorrower(registrationRequest));
        verify(borrowerRepository, never()).save(any(Borrower.class));
    }

    @Test
    void findById_WhenBorrowerExists_ShouldReturnBorrower() {
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(testBorrower));

        Borrower result = borrowerService.findById(1L);

        assertEquals(testBorrower, result);
    }

    @Test
    void findById_WhenBorrowerDoesNotExist_ShouldThrowException() {
        when(borrowerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> borrowerService.findById(99L));
    }

    @Test
    void findByBorrowerId_WhenBorrowerExists_ShouldReturnBorrower() {
        when(borrowerRepository.findByBorrowerId("BOR001")).thenReturn(Optional.of(testBorrower));

        Borrower result = borrowerService.findByBorrowerId("BOR001");

        assertEquals(testBorrower, result);
    }

    @Test
    void findByBorrowerId_WhenBorrowerDoesNotExist_ShouldThrowException() {
        when(borrowerRepository.findByBorrowerId("NONEXISTENT")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> borrowerService.findByBorrowerId("NONEXISTENT"));
    }

    @Test
    void findAllBorrowers_ShouldReturnAllBorrowers() {
        List<Borrower> borrowers = Arrays.asList(
                testBorrower,
                Borrower.builder()
                        .id(2L)
                        .borrowerId("BOR002")
                        .name("Jane Smith")
                        .email("jane.smith@example.com")
                        .build()
        );

        when(borrowerRepository.findAll()).thenReturn(borrowers);

        List<Borrower> result = borrowerService.findAllBorrowers();

        assertEquals(2, result.size());
        assertEquals(borrowers, result);
    }

    @Test
    void updateBorrower_WhenBorrowerExists_ShouldUpdateAndReturn() {
        Borrower updatedBorrower = Borrower.builder()
                .id(1L)
                .borrowerId("BOR001")
                .name("Updated Name")
                .email("updated.email@example.com")
                .build();

        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(testBorrower));
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(updatedBorrower);

        Borrower result = borrowerService.updateBorrower(1L, updatedBorrower);

        assertEquals("Updated Name", result.getName());
        assertEquals("updated.email@example.com", result.getEmail());
        verify(borrowerRepository, times(1)).save(any(Borrower.class));
    }

    @Test
    void updateBorrower_WhenBorrowerDoesNotExist_ShouldThrowException() {
        when(borrowerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> borrowerService.updateBorrower(99L, testBorrower));
        verify(borrowerRepository, never()).save(any(Borrower.class));
    }

    @Test
    void deleteBorrower_WhenBorrowerExists_ShouldDeleteBorrower() {
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(testBorrower));
        doNothing().when(borrowerRepository).delete(testBorrower);

        borrowerService.deleteBorrower(1L);

        verify(borrowerRepository, times(1)).delete(testBorrower);
    }

    @Test
    void deleteBorrower_WhenBorrowerDoesNotExist_ShouldThrowException() {
        when(borrowerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> borrowerService.deleteBorrower(99L));
        verify(borrowerRepository, never()).delete(any(Borrower.class));
    }
}