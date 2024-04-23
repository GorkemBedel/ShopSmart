package com.ShopSmart.ShopSmart.dto;

import com.ShopSmart.ShopSmart.model.Role;

import java.util.Set;

public record CreateMerchantRequest(
        String name,
        String username,
        String password,
        String companyName,
        String taxNumber


        ) {
}
