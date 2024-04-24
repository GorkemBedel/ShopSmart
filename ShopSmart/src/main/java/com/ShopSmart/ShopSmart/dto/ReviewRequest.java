package com.ShopSmart.ShopSmart.dto;

public record ReviewRequest(
        Long productId,
        Long userId,
        String review

) {
}
