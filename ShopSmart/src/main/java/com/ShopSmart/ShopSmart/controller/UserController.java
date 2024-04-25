package com.ShopSmart.ShopSmart.controller;

import com.ShopSmart.ShopSmart.dto.CreateUserRequest;
import com.ShopSmart.ShopSmart.dto.RestrictedMerchantRequest;
import com.ShopSmart.ShopSmart.dto.RestrictedUserRequest;
import com.ShopSmart.ShopSmart.dto.ReviewRequest;
import com.ShopSmart.ShopSmart.model.Review;
import com.ShopSmart.ShopSmart.model.User;
import com.ShopSmart.ShopSmart.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ShopSmart/User")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/test")
    public String UserControllerTest(@RequestParam String testParam){
        return "Test parametre = " + testParam;
    }


    //**************************************** C R U D    FOR   USER **********************************************
    @PostMapping("/createUser")
    public User createUser(@RequestBody CreateUserRequest request){
        return userService.createUser(request);
    }

    //It only shows the name and reviews of the searched user.
    @GetMapping("/findByUserUsername/{username}")
    public RestrictedUserRequest findByUsername(@PathVariable("username") String username){
        return userService.getByUserUsername(username);
    }
    @PutMapping("/updateUser/{userId}")
    public User updateUser(@PathVariable("userId") Long id, @RequestBody CreateUserRequest request){
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/deleteOwnAccount")
    public User deleteOwnAccount(){
        return userService.deleteUser();
    }
    //******************************************************************************************************************



    @GetMapping("/findByMerchantUsername/{username}")
    public RestrictedMerchantRequest findByMerchantUsername(@PathVariable("username") String username){
        return userService.getByMerchantUsername(username);
    }


    //************************************** C R U D   FOR   REVIEW ****************************************************
    @PostMapping("/review")
    public Review review(@RequestBody ReviewRequest request){

        Long productId = request.productId();
        Long userId = request.userId();
        String reviewToPost = request.review();

        return userService.reviewProduct(productId, userId, reviewToPost);
    }

    @PutMapping("/updateReview/{reviewId}")
    public Review updateReview(@PathVariable("reviewId") Long reviewId
            ,@RequestBody ReviewRequest request){
        return userService.updateReview(reviewId, request);
    }

    @DeleteMapping("/deleteReview/{reviewId}")
    public Review deleteReview(@PathVariable("reviewId") Long id){
        return userService.deleteReview(id);
    }
    //******************************************************************************************************************



}
