package com.example.library.dto.request;

import lombok.Data;
import javax.validation.constraints.*;

/**
 * Data Transfer Object for book registration requests.
 */
@Data
public class BookRequest {

	@NotBlank(message = "ISBN is mandatory")
	private String isbn;

	@NotBlank(message = "Title is mandatory")
	private String title;

	@NotBlank(message = "Author is mandatory")
	private String author;
}
