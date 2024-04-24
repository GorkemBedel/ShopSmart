package com.ShopSmart.ShopSmart.dto;

import com.ShopSmart.ShopSmart.model.Product;

import java.util.Set;

public record RestrictedMerchantRequest(
        String name,
        String companyName,
        String taxNumber,
        Set<Product>products
) {
}
