package com.ShopSmart.ShopSmart.controller;

import com.ShopSmart.ShopSmart.dto.CreateMerchantRequest;
import com.ShopSmart.ShopSmart.dto.CreateUserRequest;
import com.ShopSmart.ShopSmart.dto.ProductRequest;
import com.ShopSmart.ShopSmart.model.Merchant;
import com.ShopSmart.ShopSmart.model.Product;
import com.ShopSmart.ShopSmart.model.User;
import com.ShopSmart.ShopSmart.service.MerchantService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ShopSmart/Merchant")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping
    public String test(){
        return "Hello from Merchant Controller!";
    }


    @PostMapping("/createMerchant")
    public Merchant createMerchant(@RequestBody CreateMerchantRequest request){
        return merchantService.createMerchant(request);
    }

    @PostMapping("/addProduct")
    public Product addProduct(@RequestBody ProductRequest request){
        return merchantService.addProduct(request);
    }

    @DeleteMapping("/deleteProduct/{id}")
    public void deleteProduct(@PathVariable("id") Long id){
        merchantService.deleteProduct(id);
    }}
