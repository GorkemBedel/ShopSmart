package com.ShopSmart.ShopSmart.dto;

import com.ShopSmart.ShopSmart.model.Role;

import java.util.Set;

public record CreateUserRequest(
        String name,
        String username,
        String password
) {
}
