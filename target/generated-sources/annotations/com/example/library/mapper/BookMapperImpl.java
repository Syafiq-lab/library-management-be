package com.example.library.mapper;

import com.example.library.dto.request.BookRequest;
import com.example.library.dto.response.BookResponse;
import com.example.library.model.BookEntity;
import com.example.library.model.BorrowerEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-15T23:46:05+0800",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.9 (IBM Corporation)"
)
@Component
public class BookMapperImpl implements BookMapper {

    @Override
    public BookEntity toEntity(BookRequest bookRequest) {
        if ( bookRequest == null ) {
            return null;
        }

        BookEntity.BookEntityBuilder bookEntity = BookEntity.builder();

        bookEntity.isbn( bookRequest.getIsbn() );
        bookEntity.title( bookRequest.getTitle() );
        bookEntity.author( bookRequest.getAuthor() );

        return bookEntity.build();
    }

    @Override
    public BookResponse toResponse(BookEntity bookEntity) {
        if ( bookEntity == null ) {
            return null;
        }

        BookResponse bookResponse = new BookResponse();

        bookResponse.setBorrowerId( bookEntityBorrowerBorrowerId( bookEntity ) );
        bookResponse.setBookId( bookEntity.getBookId() );
        bookResponse.setIsbn( bookEntity.getIsbn() );
        bookResponse.setTitle( bookEntity.getTitle() );
        bookResponse.setAuthor( bookEntity.getAuthor() );

        return bookResponse;
    }

    private String bookEntityBorrowerBorrowerId(BookEntity bookEntity) {
        if ( bookEntity == null ) {
            return null;
        }
        BorrowerEntity borrower = bookEntity.getBorrower();
        if ( borrower == null ) {
            return null;
        }
        String borrowerId = borrower.getBorrowerId();
        if ( borrowerId == null ) {
            return null;
        }
        return borrowerId;
    }
}
