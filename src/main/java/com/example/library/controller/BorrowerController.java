package com.example.library.controller;

import com.example.library.dto.response.*;
import com.example.library.service.BorrowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

/**
 * REST controller for managing borrowers.
 */
@RestController
@RequestMapping("/api/borrowers")
@RequiredArgsConstructor
public class BorrowerController {

	private final BorrowerService borrowerService;

	/**
	 * Retrieves borrower details by ID. Only accessible by ADMIN role.
	 *
	 * @param borrowerId ID of the borrower
	 * @return ResponseEntity with borrower information
	 */
	@GetMapping("/{borrowerId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<BorrowerResponse>> getBorrowerById(@PathVariable String borrowerId) {
		BorrowerResponse borrowerResponse = borrowerService.getBorrowerById(borrowerId);
		ApiResponse<BorrowerResponse> response = ApiResponse.<BorrowerResponse>builder()
				.status(HttpStatus.OK.value())
				.message("Borrower retrieved successfully")
				.data(borrowerResponse)
				.timestamp(LocalDateTime.now())
				.build();
		return ResponseEntity.ok(response);
	}
}
