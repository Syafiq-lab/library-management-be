package com.example.library.mapper;

import com.example.library.dto.request.BorrowerRequest;
import com.example.library.dto.response.BorrowerResponse;
import com.example.library.model.BorrowerEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-16T17:37:14+0800",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.9 (IBM Corporation)"
)
@Component
public class BorrowerMapperImpl implements BorrowerMapper {

    @Override
    public BorrowerEntity toEntity(BorrowerRequest borrowerRequest) {
        if ( borrowerRequest == null ) {
            return null;
        }

        BorrowerEntity.BorrowerEntityBuilder borrowerEntity = BorrowerEntity.builder();

        borrowerEntity.borrowerId( borrowerRequest.getBorrowerId() );
        borrowerEntity.name( borrowerRequest.getName() );
        borrowerEntity.email( borrowerRequest.getEmail() );

        return borrowerEntity.build();
    }

    @Override
    public BorrowerResponse toResponse(BorrowerEntity borrowerEntity) {
        if ( borrowerEntity == null ) {
            return null;
        }

        BorrowerResponse borrowerResponse = new BorrowerResponse();

        borrowerResponse.setBorrowedBookIds( mapBorrowedBooks( borrowerEntity.getBorrowedBooks() ) );
        borrowerResponse.setBorrowerId( borrowerEntity.getBorrowerId() );
        borrowerResponse.setName( borrowerEntity.getName() );
        borrowerResponse.setEmail( borrowerEntity.getEmail() );

        return borrowerResponse;
    }
}
