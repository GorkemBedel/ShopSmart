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
import java.util.Optional;

@RestController
@RequestMapping("/ShopSmart/Admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/test")
    public String helloWorldPublic(){
        return "Hello world from Admin Panel";
    }

    @PostMapping("/createAdmin")
    public Admin createAdmin(@RequestBody CreateUserRequest request){
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/findByUsername/{username}")
    public Optional<User> findByUsername(@PathVariable("username") String username){
        return adminService.findByUsername(username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/findByMerchantUsername/{MerchantUsername}")
    public Optional<Merchant> findByMerchantUsername(@PathVariable("MerchantUsername") String username){
        return adminService.findByMerchantUsername(username);
    }

//    @PostMapping("/createUser")
//    public User createUser(@RequestBody CreateUserRequest request){
//        return adminService.createUser(request);
//    }
//    @PutMapping("/updateUser/{id}")
//    public List<User> updateUser(@PathVariable("id") String id, @RequestBody CreateUserRequest request){
//        return adminService.updateUser(id, request);
//    }

    @DeleteMapping("/deleteUser/{id}")
    public void deleteUser(@PathVariable("id") Long id){
        adminService.deleteUser(id);
    }

    @DeleteMapping("/deleteMerchant/{id}")
    public void deleteMerchant(@PathVariable("id") Long id){
        adminService.deleteMerchant(id);
    }

}
