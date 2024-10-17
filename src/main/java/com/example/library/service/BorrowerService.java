package com.example.library.service;

import com.example.library.dto.response.BorrowerResponse;

import java.util.List;

/**
 * Service interface for managing borrowers.
 */
public interface BorrowerService {
	BorrowerResponse getBorrowerById(String borrowerId);
	List<BorrowerResponse> getAllBorrowers();
}
