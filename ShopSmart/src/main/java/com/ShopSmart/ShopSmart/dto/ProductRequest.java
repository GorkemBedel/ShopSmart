package com.ShopSmart.ShopSmart.dto;

public record ProductRequest(
        Long merchantId,
        String productName,
        Long productStock,
        String productDescription
) {
}
