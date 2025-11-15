package com.example.userservice.mapping;

import com.example.userservice.domain.User;
import com.example.userservice.web.dto.UserCreateRequest;
import com.example.userservice.web.dto.UserResponse;
import com.example.userservice.web.dto.UserUpdateRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UserUpdateRequest request, @MappingTarget User user);

    UserResponse toResponse(User user);
}
