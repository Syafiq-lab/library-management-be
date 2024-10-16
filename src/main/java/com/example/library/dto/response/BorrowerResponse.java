package com.example.library.dto.response;

import lombok.Data;
import java.util.Set;

/**
 * Data Transfer Object for borrower responses.
 */
@Data
public class BorrowerResponse {

	private String borrowerId;
	private String name;
	private String email;
	private Set<String> borrowedBookIds;
}
