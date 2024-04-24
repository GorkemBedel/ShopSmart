package com.ShopSmart.ShopSmart.service;

import com.ShopSmart.ShopSmart.dto.*;
import com.ShopSmart.ShopSmart.exceptions.EmptyPasswordException;
import com.ShopSmart.ShopSmart.exceptions.UnauthorizedException;
import com.ShopSmart.ShopSmart.exceptions.UsernameNotUniqueException;
import com.ShopSmart.ShopSmart.model.*;
import com.ShopSmart.ShopSmart.repository.MerchantRepository;
import com.ShopSmart.ShopSmart.repository.ProductRepository;
import com.ShopSmart.ShopSmart.repository.ReviewRepository;
import com.ShopSmart.ShopSmart.repository.UserRepository;
import com.ShopSmart.ShopSmart.rules.PasswordValidator;
import com.ShopSmart.ShopSmart.rules.UniqueUsernameValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UniqueUsernameValidator uniqueUsernameValidator;
    private final PasswordValidator passwordValidator;


    public UserService(UserRepository userRepository, MerchantRepository merchantRepository
            , BCryptPasswordEncoder bCryptPasswordEncoder, UniqueUsernameValidator uniqueUsernameValidator
            , PasswordValidator passwordValidator, ProductRepository productRepository
            , ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.uniqueUsernameValidator = uniqueUsernameValidator;
        this.passwordValidator = passwordValidator;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    public User createUser(CreateUserRequest createUserRequest) throws UsernameNotUniqueException {

        //Checking if that username is existed in database
        String username = createUserRequest.username();
        uniqueUsernameValidator.validateUsername(username);

        //Checking if the password is valid
        String password = createUserRequest.password();
        passwordValidator.validatePassword(password);



        User newUser = User.builder()
                .name(createUserRequest.name())
                .username(createUserRequest.username())
                .password(bCryptPasswordEncoder.encode(createUserRequest.password()))
                .role(Role.ROLE_USER)
                .authorities(new HashSet<>(List.of(Role.ROLE_USER)))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .isEnabled(true)
                .build();

        return userRepository.save(newUser);
    }



    public RestrictedUserRequest getByUserUsername(String userName){
        Optional<User> user = userRepository.findByusername(userName);
        if(user.isPresent()){
            RestrictedUserRequest request = new RestrictedUserRequest(user.get().getName());
            return request;
        }else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public RestrictedMerchantRequest getByMerchantUsername(String userName){
        Optional<Merchant> merchant = merchantRepository.findByusername(userName);
        if(merchant.isPresent()){

            //DTO transformation
            RestrictedMerchantRequest request = new RestrictedMerchantRequest(
                    merchant.get().getName()
                    ,merchant.get().getCompanyName()
                    ,merchant.get().getTaxNumber()
                    ,merchant.get().getProducts()  );
            return request;
        }else {
            throw new UsernameNotFoundException("Merchant not found");
        }
    }

    public Review reviewProduct(Long productId, Long userId, String reviewToPost){
        Optional<Product> reviewedProduct = productRepository.findById(productId);
        Optional<User> reviewedUser = userRepository.findById(userId);

        //Authentication check
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();



        if(reviewedProduct.isPresent() && reviewedUser.isPresent()) {

            if (!reviewedUser.get().getUsername().equals(loggedInUsername)) {
                throw new UnauthorizedException("You are trying to write a review from another user's account.");
            }
            //If that person and that product are present create a review object
            Review reviewToWrite = Review.builder()
                    .review(reviewToPost)
                    .product(reviewedProduct.get())
                    .user(reviewedUser.get())
                    .build();

            //Add that review through review repository
            reviewRepository.save(reviewToWrite);

            //Add that review object through the products reviews list
            Set<Review> productsReviews = reviewedProduct.get().getReviews();
            productsReviews.add(reviewToWrite);
            reviewedProduct.get().setReviews(productsReviews);

            //Add that review object through the users reviews list
            Set<Review> usersReviews = reviewedUser.get().getReviews();
            usersReviews.add(reviewToWrite);
            reviewedUser.get().setReviews(usersReviews);


            return reviewToWrite;
        }else {
            throw new UsernameNotFoundException("Product or user not found");
        }
    }

    public Review updateReview(Long reviewId, ReviewRequest request){
        Optional<Review> updatedReview = reviewRepository.findById(reviewId);
        Optional<Product> updatedProduct = productRepository.findById(request.productId());

        if(updatedReview.isPresent()){

            //Checking if the user is trying to update his/her OWN review
            User user = updatedReview.get().getUser();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String loggedInUsername = authentication.getName();
            if (!user.getUsername().equals(loggedInUsername)) {
                throw new UnauthorizedException("You are trying to update another user's informations.");
            }
            //

            updatedReview.get().setReview(request.review());
            if(updatedProduct.isPresent()) {
                updatedReview.get().setProduct(updatedProduct.get());
                updatedReview.get().setReview(request.review());
            }
            return reviewRepository.save(updatedReview.get());
        } else {
            throw new UsernameNotFoundException("Product Id not found");
        }
    }

    public User updateUser(Long userId, CreateUserRequest request){
        Optional<User> updatedUser = userRepository.findById(userId);

        //Checking if the user is trying to update his/her OWN account
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();



        if(updatedUser.isPresent() ){
            if (!updatedUser.get().getUsername().equals(loggedInUsername)) {
                throw new UnauthorizedException("You are trying to update another user's informations.");
            }
            updatedUser.get().setName(request.name());
            updatedUser.get().setPassword(request.password());
            return userRepository.save(updatedUser.get());
        }else {
            throw new UsernameNotFoundException("Product Id not found");
        }
    }




}
