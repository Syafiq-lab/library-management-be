package com.example.userservice.service;

import com.example.common.exception.UserNotFoundException;
import com.example.userservice.domain.User;
import com.example.userservice.dto.UserCreateRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.dto.UserUpdateRequest;
import com.example.userservice.mapping.UserMapper;
import com.example.userservice.messaging.UserEventPublisher;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserEventPublisher eventPublisher;

    public UserResponse createUser(UserCreateRequest request) {
        log.debug("createUser called: username={}, emailPresent={}",
                request.getUsername(), request.getEmail() != null);

        User user = userMapper.toEntity(request);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        User saved = userRepository.save(user);
        log.debug("User saved: id={}, username={}", saved.getId(), saved.getUsername());

        eventPublisher.publishUserCreated(saved);
        log.debug("UserCreatedEvent queued for publish: id={}", saved.getId());

        return userMapper.toResponse(saved);
    }

    public UserResponse getUser(Long id) {
        log.debug("getUser called: id={}", id);
        return userMapper.toResponse(findByIdOrThrow(id));
    }

    public List<UserResponse> getAllUsers() {
        log.debug("getAllUsers called");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        log.debug("updateUser called: id={}, emailPresent={}",
                id, request.getEmail() != null);

        User user = findByIdOrThrow(id);
        userMapper.updateEntity(request, user);
        user.setUpdatedAt(Instant.now());

        User saved = userRepository.save(user);
        log.debug("User updated and saved: id={}, username={}", saved.getId(), saved.getUsername());

        eventPublisher.publishUserUpdated(saved);
        log.debug("UserUpdatedEvent queued for publish: id={}", saved.getId());

        return userMapper.toResponse(saved);
    }

    public void deleteUser(Long id) {
        log.debug("deleteUser called: id={}", id);

        User user = findByIdOrThrow(id);
        userRepository.delete(user);

        log.info("User deleted: id={}, username={}", user.getId(), user.getUsername());
        // you can add a UserDeletedEvent later
    }

    private User findByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", id);
                    return new UserNotFoundException(id);
                });
    }
}
