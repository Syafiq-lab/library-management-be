    package com.mb.transactionbackend.controller;

    import com.mb.transactionbackend.dto.BorrowerRegistrationRequest;
    import com.mb.transactionbackend.model.Borrower;
    import com.mb.transactionbackend.service.BorrowerService;
    import com.mb.transactionbackend.dto.ApiResponse;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @Slf4j
    @RestController
    @RequestMapping("/api/borrowers")
    @RequiredArgsConstructor
    public class BorrowerController {

        private final BorrowerService borrowerService;

        @PostMapping
        public ResponseEntity<ApiResponse<Borrower>> registerBorrower(
                @Valid @RequestBody BorrowerRegistrationRequest request) {
            log.info("Received request to register borrower with ID: {}, email: {}",
                    request.borrowerId(), request.email());

            Borrower borrower = borrowerService.registerBorrower(request);
            log.info("Successfully registered borrower with ID: {}", borrower.getBorrowerId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Borrower registered", borrower));
        }
    }