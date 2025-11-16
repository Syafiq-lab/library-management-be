package com.example.userservice.service;

import com.example.userservice.domain.User;
import com.example.userservice.mapping.UserMapper;
import com.example.userservice.messaging.UserEventPublisher;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.web.dto.UserCreateRequest;
import com.example.userservice.web.dto.UserResponse;
import com.example.userservice.web.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserEventPublisher eventPublisher;

    public UserResponse createUser(UserCreateRequest request) {
        User user = userMapper.toEntity(request);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        User saved = userRepository.save(user);
        eventPublisher.publishUserCreated(saved);

        return userMapper.toResponse(saved);
    }

    public UserResponse getUser(Long id) {
        return userMapper.toResponse(findByIdOrThrow(id));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = findByIdOrThrow(id);
        userMapper.updateEntity(request, user);
        user.setUpdatedAt(Instant.now());

        User saved = userRepository.save(user);
        eventPublisher.publishUserUpdated(saved);

        return userMapper.toResponse(saved);
    }

    public void deleteUser(Long id) {
        User user = findByIdOrThrow(id);
        userRepository.delete(user);
        // you can add a UserDeletedEvent later
    }

    private User findByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found " + id));
    }
}
