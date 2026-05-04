package com.example.demo.user.dto;

import com.example.demo.user.entity.Role;

public class RoleUpdateRequest {
    private Role role;

    public RoleUpdateRequest() {
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

