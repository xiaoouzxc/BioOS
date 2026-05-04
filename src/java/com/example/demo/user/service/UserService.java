package com.example.demo.user.service;

import com.example.demo.user.dto.RegisterRequest;
import com.example.demo.user.dto.UserResponse;
import com.example.demo.user.entity.Role;
import com.example.demo.user.entity.UserAccount;

import java.util.List;

public interface UserService {
    UserAccount register(RegisterRequest request);

    UserAccount findByUsername(String username);

    List<UserResponse> findAllUsers();

    UserResponse updateUserRole(Long userId, Role role);

    UserResponse toResponse(UserAccount user);
}

