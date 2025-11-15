package com.example.userservice.web;

import com.example.common.api.PageResponse;
import com.example.userservice.service.UserService;
import com.example.userservice.web.dto.UserCreateRequest;
import com.example.userservice.web.dto.UserResponse;
import com.example.userservice.web.dto.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user")
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping
    @Operation(summary = "List all users")
    public PageResponse<UserResponse> getUsers() {
        List<UserResponse> users = userService.getAllUsers();
        PageImpl<UserResponse> page = new PageImpl<>(users);
        return PageResponse.<UserResponse>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user by id")
    public UserResponse updateUser(@PathVariable Long id,
                                   @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user by id")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
