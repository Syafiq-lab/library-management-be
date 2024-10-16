package com.example.library.mapper;

import com.example.library.dto.request.BookRequest;
import com.example.library.dto.response.BookResponse;
import com.example.library.model.BookEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between BookEntity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface BookMapper {

	/**
	 * Converts BookRequest DTO to BookEntity.
	 *
	 * @param bookRequest BookRequest instance
	 * @return BookEntity instance
	 */
	@Mapping(target = "bookId", ignore = true)
	@Mapping(target = "borrower", ignore = true)
	BookEntity toEntity(BookRequest bookRequest);

	/**
	 * Converts BookEntity to BookResponse DTO.
	 *
	 * @param bookEntity BookEntity instance
	 * @return BookResponse instance
	 */
	@Mapping(source = "borrower.borrowerId", target = "borrowerId")
	BookResponse toResponse(BookEntity bookEntity);
}
