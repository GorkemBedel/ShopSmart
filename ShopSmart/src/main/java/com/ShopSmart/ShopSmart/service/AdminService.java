package com.ShopSmart.ShopSmart.service;

import com.ShopSmart.ShopSmart.dto.CreateMerchantRequest;
import com.ShopSmart.ShopSmart.dto.CreateUserRequest;
import com.ShopSmart.ShopSmart.dto.ReviewRequest;
import com.ShopSmart.ShopSmart.exceptions.UnauthorizedException;
import com.ShopSmart.ShopSmart.exceptions.UsernameNotUniqueException;
import com.ShopSmart.ShopSmart.model.*;
import com.ShopSmart.ShopSmart.repository.AdminRepository;
import com.ShopSmart.ShopSmart.repository.MerchantRepository;
import com.ShopSmart.ShopSmart.repository.ReviewRepository;
import com.ShopSmart.ShopSmart.repository.UserRepository;
import com.ShopSmart.ShopSmart.rules.PasswordValidator;
import com.ShopSmart.ShopSmart.rules.UniqueUsernameValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final ReviewRepository reviewRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UniqueUsernameValidator uniqueUsernameValidator;
    private final PasswordValidator passwordValidator;


    public AdminService(AdminRepository adminRepository, UserRepository userRepository
                        , MerchantRepository merchantRepository
                        , BCryptPasswordEncoder bCryptPasswordEncoder, UniqueUsernameValidator uniqueUsernameValidator
                        , PasswordValidator passwordValidator, ReviewRepository reviewRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.uniqueUsernameValidator = uniqueUsernameValidator;
        this.passwordValidator = passwordValidator;
        this.reviewRepository = reviewRepository;
    }

    public Admin createAdmin(CreateUserRequest createUserRequest){

        //Checking if that username is exist in database
        String username = createUserRequest.username();
        uniqueUsernameValidator.validateUsername(username);

        //Checking if the password is valid
        String password = createUserRequest.password();
        passwordValidator.validatePassword(password);

        Admin newAdmin = Admin.builder()
                .name(createUserRequest.name())
                .username(createUserRequest.username())
                .password(bCryptPasswordEncoder.encode(createUserRequest.password()))
                .role(Role.ROLE_ADMIN)
                .authorities(new HashSet<>(List.of(Role.ROLE_ADMIN)))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .isEnabled(true)
                .build();

        return adminRepository.save(newAdmin);
    }

    public Optional<Admin> getByAdminName(String adminName){
        return adminRepository.findByusername(adminName);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username){
        return userRepository.findByusername(username);
    }

    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    public Optional<Merchant> findByMerchantUsername(String username){
        return merchantRepository.findByusername(username);
    }

    public User deleteUser(Long userId){
        Optional<User> deletedUser = userRepository.findById(userId);
        if(deletedUser.isPresent()) {
            userRepository.delete(deletedUser.get());
        }
        return deletedUser.orElseThrow(() -> new UsernameNotFoundException("There is no user with id: " + userId));
    }

    public Merchant deleteMerchant(Long merchantId){
        Optional<Merchant> deletedMerchant = merchantRepository.findById(merchantId);
        if(deletedMerchant.isPresent()) {
            merchantRepository.delete(deletedMerchant.get());
        }
        return deletedMerchant.orElseThrow(() -> new UsernameNotFoundException("There is no merchant with id: " + merchantId));
    }


    public User createUser(CreateUserRequest createUserRequest) {

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

    public User updateUser(Long userId, CreateUserRequest request) {
        Optional<User> updatedUser = userRepository.findById(userId);


        if(updatedUser.isPresent() ){
            updatedUser.get().setName(request.name());
            updatedUser.get().setPassword(request.password());
            return userRepository.save(updatedUser.get());
        }else {
            throw new UsernameNotFoundException("User Id not found");
        }
    }

    public Merchant createMerchant(CreateMerchantRequest createMerchantRequest) {

        //Checking if that username is exist in database
        String username = createMerchantRequest.username();
        uniqueUsernameValidator.validateUsername(username);

        //Checking if the password is valid
        String password = createMerchantRequest.password();
        passwordValidator.validatePassword(password);

        Merchant newMerchant = Merchant.builder()
                .name(createMerchantRequest.name())
                .username(createMerchantRequest.username())
                .password(bCryptPasswordEncoder.encode(createMerchantRequest.password()))
                .role(Role.ROLE_MERCHANT)
                .authorities(new HashSet<>(List.of(Role.ROLE_MERCHANT)))
                .companyName(createMerchantRequest.companyName())
                .taxNumber(createMerchantRequest.taxNumber())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .isEnabled(true)
                .build();

        return merchantRepository.save(newMerchant);
    }

    public Merchant updateMerchant(Long id, CreateMerchantRequest request) {
        Optional<Merchant> updatedMerchant = merchantRepository.findById(id);


        if(updatedMerchant.isPresent() ){
            updatedMerchant.get().setName(request.name());
            updatedMerchant.get().setPassword(request.password());
            updatedMerchant.get().setCompanyName(request.companyName());
            updatedMerchant.get().setTaxNumber(request.taxNumber());

            return merchantRepository.save(updatedMerchant.get());
        }else {
            throw new UsernameNotFoundException("Merchant Id not found");
        }
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Admin deleteAdmin(Long id) {
        Optional<Admin> deletedAdmin = adminRepository.findById(id);
        if(deletedAdmin.isPresent()) {
            adminRepository.delete(deletedAdmin.get());
        }
        return deletedAdmin.orElseThrow(() -> new UsernameNotFoundException("There is no admin with id: " + id));
    }

    public Admin updateAdmin(Long id, CreateUserRequest request) {
        Optional<Admin> updatedAdmin = adminRepository.findById(id);


        if(updatedAdmin.isPresent() ){
            updatedAdmin.get().setName(request.name());
            updatedAdmin.get().setPassword(request.password());
            return adminRepository.save(updatedAdmin.get());
        }else {
            throw new UsernameNotFoundException("Admin Id not found");
        }
    }

    public Review deleteReview(Long id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);

        if (reviewOptional.isPresent()) {
            Merchant ownerOfTheReviewedProduct = reviewOptional.get().getProduct().getMerchant();
            reviewRepository.delete(reviewOptional.get());
            return reviewOptional.get();

        } else {
            throw new UsernameNotUniqueException("There is no review with id: " + id);
        }
    }



}
