package com.ShopSmart.ShopSmart.service;

import com.ShopSmart.ShopSmart.dto.CreateUserRequest;
import com.ShopSmart.ShopSmart.model.Admin;
import com.ShopSmart.ShopSmart.model.Merchant;
import com.ShopSmart.ShopSmart.model.Role;
import com.ShopSmart.ShopSmart.model.User;
import com.ShopSmart.ShopSmart.repository.AdminRepository;
import com.ShopSmart.ShopSmart.repository.MerchantRepository;
import com.ShopSmart.ShopSmart.repository.UserRepository;
import com.ShopSmart.ShopSmart.rules.PasswordValidator;
import com.ShopSmart.ShopSmart.rules.UniqueUsernameValidator;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UniqueUsernameValidator uniqueUsernameValidator;
    private final PasswordValidator passwordValidator;


    public AdminService(AdminRepository adminRepository, UserRepository userRepository
                        , MerchantRepository merchantRepository
                        , BCryptPasswordEncoder bCryptPasswordEncoder, UniqueUsernameValidator uniqueUsernameValidator
                        , PasswordValidator passwordValidator) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.uniqueUsernameValidator = uniqueUsernameValidator;
        this.passwordValidator = passwordValidator;
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






}
