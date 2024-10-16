package com.example.library.mapper;

import com.example.library.dto.request.BorrowerRequest;
import com.example.library.dto.response.BorrowerResponse;
import com.example.library.model.BorrowerEntity;
import com.example.library.model.BookEntity;
import org.mapstruct.*;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper interface for converting between BorrowerEntity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface BorrowerMapper {

	/**
	 * Converts BorrowerRequest DTO to BorrowerEntity.
	 *
	 * @param borrowerRequest BorrowerRequest instance
	 * @return BorrowerEntity instance
	 */
	@Mapping(target = "borrowedBooks", ignore = true)
	@Mapping(target = "user", ignore = true)
	BorrowerEntity toEntity(BorrowerRequest borrowerRequest);

	/**
	 * Converts BorrowerEntity to BorrowerResponse DTO.
	 *
	 * @param borrowerEntity BorrowerEntity instance
	 * @return BorrowerResponse instance
	 */
	@Mapping(source = "borrowedBooks", target = "borrowedBookIds", qualifiedByName = "mapBorrowedBooks")
	BorrowerResponse toResponse(BorrowerEntity borrowerEntity);

	/**
	 * Custom method to map borrowed books to their IDs.
	 *
	 * @param borrowedBooks Set of BookEntity instances
	 * @return Set of book IDs
	 */
	@Named("mapBorrowedBooks")
	default Set<String> mapBorrowedBooks(Set<BookEntity> borrowedBooks) {
		if (borrowedBooks == null) {
			return null;
		}
		return borrowedBooks.stream()
				.map(BookEntity::getBookId)
				.collect(Collectors.toSet());
	}
}
