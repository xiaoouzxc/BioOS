package com.example.demo.user.controller;

import com.example.demo.user.dto.RoleUpdateRequest;
import com.example.demo.user.dto.UserResponse;
import com.example.demo.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.findAllUsers();
    }

    @PutMapping("/{id}/role")
    public UserResponse updateUserRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        return userService.updateUserRole(id, request.getRole());
    }
}

