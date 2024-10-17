package com.example.library.service.impl;

import com.example.library.dto.response.BorrowerResponse;
import com.example.library.exception.CustomException;
import com.example.library.mapper.BorrowerMapper;
import com.example.library.model.BorrowerEntity;
import com.example.library.repository.BorrowerRepository;
import com.example.library.service.BorrowerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of BorrowerService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BorrowerServiceImpl implements BorrowerService {

	private final BorrowerRepository borrowerRepository;
	private final BorrowerMapper borrowerMapper;

	/**
	 * Retrieves borrower details by ID.
	 *
	 * @param borrowerId ID of the borrower
	 * @return BorrowerResponse with borrower information
	 */
	@Override
	public BorrowerResponse getBorrowerById(String borrowerId) {
		BorrowerEntity borrower = borrowerRepository.findById(borrowerId)
				.orElseThrow(() -> new CustomException("Borrower not found", HttpStatus.NOT_FOUND));

		log.info("Retrieved borrower '{}'", borrower.getName());

		return borrowerMapper.toResponse(borrower);
	}

	/**
	 * Retrieves all borrowers.
	 *
	 * @return List of BorrowerResponse with borrower information
	 */
	@Override
	public List<BorrowerResponse> getAllBorrowers() {
		List<BorrowerEntity> borrowers = borrowerRepository.findAll();

		log.info("Retrieved {} borrowers", borrowers.size());

		return borrowers.stream()
				.map(borrowerMapper::toResponse)
				.collect(Collectors.toList());
	}
}
