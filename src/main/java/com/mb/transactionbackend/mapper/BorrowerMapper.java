package com.mb.transactionbackend.mapper;

import com.mb.transactionbackend.dto.BorrowerRegistrationRequest;
import com.mb.transactionbackend.model.Borrower;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BorrowerMapper {
    
    @Mapping(target = "id", ignore = true)
    Borrower toEntity(BorrowerRegistrationRequest request);
}