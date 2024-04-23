package com.ShopSmart.ShopSmart.controller;

import com.ShopSmart.ShopSmart.dto.CreateUserRequest;
import com.ShopSmart.ShopSmart.model.User;
import com.ShopSmart.ShopSmart.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ShopSmart")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public String UserControllerTest(@RequestParam String testParam){
        return "Test parametre = " + testParam;
    }

    @PostMapping("/createUser")
    public User createUser(@RequestBody CreateUserRequest request){
        return userService.createUser(request);
    }

    @GetMapping("/findByUsername/{username}")
    public Optional<User> findByUsername(@PathVariable("username") String username){
        return userService.getByUserName(username);
    }



}