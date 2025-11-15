package com.example.userservice.service;

import com.example.userservice.domain.User;
import com.example.userservice.mapping.UserMapper;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.web.dto.UserCreateRequest;
import com.example.userservice.web.dto.UserResponse;
import com.example.userservice.web.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        User user = userMapper.toEntity(request);
        user.setActive(true);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userMapper.updateEntity(request, user);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }
}
