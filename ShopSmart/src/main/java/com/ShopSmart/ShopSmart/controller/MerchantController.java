package com.ShopSmart.ShopSmart.controller;

import com.ShopSmart.ShopSmart.dto.CreateMerchantRequest;
import com.ShopSmart.ShopSmart.dto.CreateUserRequest;
import com.ShopSmart.ShopSmart.dto.ProductRequest;
import com.ShopSmart.ShopSmart.model.Merchant;
import com.ShopSmart.ShopSmart.model.Product;
import com.ShopSmart.ShopSmart.model.User;
import com.ShopSmart.ShopSmart.service.MerchantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    //****************************** C R U D     FOR    MERCHANT********************************************************
    @PostMapping("/createMerchant")
    public Merchant createMerchant(@RequestBody CreateMerchantRequest request){
        return merchantService.createMerchant(request);
    }

    @DeleteMapping("/deleteOwnAccount")
    public Merchant deleteOwnAccount(){
        return merchantService.deleteMerchant();
    }
    //******************************************************************************************************************


    @PostMapping("/addProduct")
    public Product addProduct(@RequestBody ProductRequest request){
        return merchantService.addProduct(request);
    }

    @GetMapping("/allProducts")
    public List<Product> allProducts(){
        return merchantService.getAllProducts();
    }

    @PutMapping("/updateProduct/{productId}")
    public Product updateProduct(@PathVariable("productId") Long id, @RequestBody ProductRequest updateRequest){
        return merchantService.updateProduct(id, updateRequest);
    }

    @DeleteMapping("/deleteProduct/{productId}")
    public Product deleteProduct(@PathVariable("productId") Long id){
        return merchantService.deleteProduct(id);
    }}
