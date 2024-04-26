package com.ShopSmart.ShopSmart.controller;

import com.ShopSmart.ShopSmart.dto.CreateMerchantRequest;
import com.ShopSmart.ShopSmart.dto.CreateUserRequest;
import com.ShopSmart.ShopSmart.model.Admin;
import com.ShopSmart.ShopSmart.model.Merchant;
import com.ShopSmart.ShopSmart.model.Review;
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


    //**************************************** C R U D    FOR    ADMIN *************************************************
    @PostMapping("/createAdmin")
    public Admin createAdmin(@RequestBody CreateUserRequest request){
        return adminService.createAdmin(request);
    }

    @GetMapping("/GetAdmins")
    public List<Admin> getAdmins(){
        return adminService.getAllAdmins();
    }

    @PutMapping("/updateAdmin/{adminId}")
    public Admin updateAdmin(@PathVariable("adminId") Long id, @RequestBody CreateUserRequest request){
        return adminService.updateAdmin(id, request);
    }

    @DeleteMapping("/deleteAdmin/{adminId}")
    public Admin deleteAdmin(@PathVariable("adminId") Long id){
        return adminService.deleteAdmin(id);
    }
    //******************************************************************************************************************



    //****************************************** C R U D   FOR   USER **************************************************
    @PostMapping("/createUser")
    public User createUser(@RequestBody CreateUserRequest request){
        return adminService.createUser(request);
    }
    @GetMapping("/findByUsername/{username}")
    public Optional<User> findByUsername(@PathVariable("username") String username){
        return adminService.findByUsername(username);
    }

    @GetMapping("/GetUsers")
    public List<User> getUsers() {
        return adminService.getAllUsers();
    }

    @PutMapping("/updateUser/{userId}")
    public User updateUser(@PathVariable("userId") Long id, @RequestBody CreateUserRequest request){
        return adminService.updateUser(id, request);
    }

    @DeleteMapping("/deleteUser/{id}")
    public User deleteUser(@PathVariable("id") Long id){
        return adminService.deleteUser(id);
    }
    //******************************************************************************************************************





    //***************************************** C R U D    FOR    MERCHANT**********************************************
    @PostMapping("/createMerchant")
    public Merchant createMerchant(@RequestBody CreateMerchantRequest request){
        return adminService.createMerchant(request);
    }
    @GetMapping("/findByMerchantUsername/{MerchantUsername}")
    public Optional<Merchant> findByMerchantUsername(@PathVariable("MerchantUsername") String username){
        return adminService.findByMerchantUsername(username);
    }

    @GetMapping("/GetMerchants")
    public List<Merchant> getMerchants() {
        return adminService.getAllMerchants();
    }

    @PutMapping("/updateMerchant/{merchantId}")
    public Merchant updateMerchant(@PathVariable("merchantId") Long id, @RequestBody CreateMerchantRequest request){
        return adminService.updateMerchant(id, request);
    }

    @DeleteMapping("/deleteMerchant/{id}")
    public Merchant deleteMerchant(@PathVariable("id") Long id){return adminService.deleteMerchant(id);
    }
    //******************************************************************************************************************


    //***************************************** C R U D    FOR    REVIEW************************************************
    @DeleteMapping("/deleteReview/{reviewId}")
    public Review deleteReview(@PathVariable("reviewId") Long id){return adminService.deleteReview(id);
    }
    //******************************************************************************************************************



}
