package com.ShopSmart.ShopSmart.dto;

import com.ShopSmart.ShopSmart.model.Review;

import java.util.Set;

public record RestrictedUserRequest(
        String name,
        Set<Review> reviews

) {
}
