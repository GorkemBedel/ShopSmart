package com.ShopSmart.ShopSmart.service;

import com.ShopSmart.ShopSmart.dto.*;
import com.ShopSmart.ShopSmart.exceptions.EmptyPasswordException;
import com.ShopSmart.ShopSmart.exceptions.UnauthorizedException;
import com.ShopSmart.ShopSmart.exceptions.UsernameNotUniqueException;
import com.ShopSmart.ShopSmart.model.*;
import com.ShopSmart.ShopSmart.repository.*;
import com.ShopSmart.ShopSmart.rules.PasswordValidator;
import com.ShopSmart.ShopSmart.rules.UniqueUsernameValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public Review reviewProduct(Long productId, String reviewToPost){
        Optional<Product> reviewedProduct = productRepository.findById(productId);

        //Authentication check
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();

        Optional<User> reviewedUser = userRepository.findByusername(loggedInUsername);



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
            updatedUser.get().setPassword(bCryptPasswordEncoder.encode(request.password()));

            return userRepository.save(updatedUser.get());
        }else {
            throw new UsernameNotFoundException("Product Id not found");
        }
    }


    public Review deleteReview(Long reviewId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();

        Optional<Review> deletedReview = reviewRepository.findById(reviewId);
        if(deletedReview.isPresent()) {

            //Reviewın sahibi user
            User user = deletedReview.get().getUser();
            if(!user.getUsername().equals(loggedInUsername)){
                throw new UnauthorizedException("You are trying to delete another user's review.");
            }

            //Userın review listesinden silindi
            Set<Review> reviews = user.getReviews();
            reviews.remove(deletedReview.get());
            user.setReviews(reviews);

            //review repositoryden silindi
            reviewRepository.delete(deletedReview.get());
        }
        return deletedReview.orElseThrow(() -> new UsernameNotUniqueException("There is no review with id: "+reviewId));
    }


    public User deleteUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();
        Optional<User> userOptional = userRepository.findByusername(loggedInUsername);

        if (userOptional.isPresent()) {
            User deletedUser = userOptional.get();
            userRepository.delete(deletedUser);
            return deletedUser;
        } else {
            throw new UsernameNotFoundException("Deleted user can not be found");
        }
    }

//    public Product addBox(Long productId){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String loggedInUsername = authentication.getName();
//        Optional<User> userOptional = userRepository.findByusername(loggedInUsername);
//
//        Optional<Product> productOptional = productRepository.findById(productId);
//
//        if (userOptional.isPresent() && productOptional.isPresent()) {
//
//            Box box = userOptional.get().getBox();
//            if (box == null) {
//                box = Box.builder()
//                        .products(new HashSet<>())
//                        .user(userOptional.get())
//                        .build();
//                userOptional.get().setBox(box);
//            }
//
//            box.getProducts().add(productOptional.get());
//            boxRepository.save(box);
//            userRepository.save(userOptional.get()); // Kullanıcı nesnesini güncelle
//
//
//            return productOptional.get();
//        }else{
//            throw new UsernameNotUniqueException("There is no product with id: " + productId);
//        }
//    }
}
