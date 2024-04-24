package com.ShopSmart.ShopSmart.controller;

import com.ShopSmart.ShopSmart.dto.CreateUserRequest;
import com.ShopSmart.ShopSmart.model.Admin;
import com.ShopSmart.ShopSmart.model.Merchant;
import com.ShopSmart.ShopSmart.model.User;
import com.ShopSmart.ShopSmart.service.AdminService;
import com.ShopSmart.ShopSmart.service.MerchantService;
import com.ShopSmart.ShopSmart.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ShopSmart/Admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final MerchantService merchantService;

    public AdminController(AdminService adminService, UserService userService, MerchantService merchantService) {
        this.adminService = adminService;
        this.userService = userService;
        this.merchantService = merchantService;
    }



    @GetMapping("/test")
    public String helloWorldPublic(){
        return "Hello world from Admin Panel";
    }

    @PostMapping("/createAdmin")
    public Admin createUser(@RequestBody CreateUserRequest request){
        return adminService.createAdmin(request);
    }

    @GetMapping("/GetUsers")
    public List<User> getUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/GetMerchants")
    public List<Merchant> getMerchants() {
        return adminService.getAllMerchants();
    }

    @DeleteMapping("/deleteUser/{id}")
    public void deleteUser(@PathVariable("id") Long id){
        adminService.deleteUser(id);
    }

    @DeleteMapping("/deleteMerchant/{id}")
    public void deleteMerchant(@PathVariable("id") Long id){
        adminService.deleteMerchant(id);
    }

}
