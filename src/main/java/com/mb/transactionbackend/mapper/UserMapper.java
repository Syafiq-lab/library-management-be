package com.mb.transactionbackend.mapper;

import com.mb.transactionbackend.dto.UserRegistrationRequest;
import com.mb.transactionbackend.enums.RoleEnum;
import com.mb.transactionbackend.model.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Set;

@Mapper(componentModel = "spring", imports = {Instant.class, Set.class, RoleEnum.class})
public interface UserMapper {

	@Mapping(target = "username", expression = "java(request.getUsername().trim().toLowerCase())")
	@Mapping(target = "password", expression = "java(passwordEncoder.encode(request.getPassword()))")
	@Mapping(target = "roles", expression = "java(Set.of(RoleEnum.ROLE_USER))")
	@Mapping(target = "deleted", constant = "false")
	@Mapping(target = "createdAt", expression = "java(Instant.now())")
	@Mapping(target = "id", ignore = true)
	User toEntity(UserRegistrationRequest request, @Context PasswordEncoder passwordEncoder);
}