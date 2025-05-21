package com.mb.transactionbackend.service;


import com.mb.transactionbackend.dto.AuthRequest;
import com.mb.transactionbackend.dto.UserRegistrationRequest;
import com.mb.transactionbackend.model.User;

public interface UserService {
	User registerUser(UserRegistrationRequest request);
	String authenticateUser(AuthRequest request);
	User getCurrentUser();
	void deleteUser(Long userId);
}