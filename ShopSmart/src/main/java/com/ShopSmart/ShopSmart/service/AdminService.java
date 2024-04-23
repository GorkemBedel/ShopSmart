package com.ShopSmart.ShopSmart.service;

import com.ShopSmart.ShopSmart.dto.CreateUserRequest;
import com.ShopSmart.ShopSmart.model.Admin;
import com.ShopSmart.ShopSmart.model.Merchant;
import com.ShopSmart.ShopSmart.model.Role;
import com.ShopSmart.ShopSmart.model.User;
import com.ShopSmart.ShopSmart.repository.AdminRepository;
import com.ShopSmart.ShopSmart.repository.MerchantRepository;
import com.ShopSmart.ShopSmart.repository.UserRepository;
import com.ShopSmart.ShopSmart.rules.UniqueUsernameValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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


    public AdminService(AdminRepository adminRepository, UserRepository userRepository, MerchantRepository merchantRepository,
                        BCryptPasswordEncoder bCryptPasswordEncoder, UniqueUsernameValidator uniqueUsernameValidator) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.uniqueUsernameValidator = uniqueUsernameValidator;
    }

    public Admin createAdmin(CreateUserRequest createUserRequest){

        //Checking if that username is exists in database
        String username = createUserRequest.username();
        uniqueUsernameValidator.validateUsername(username);

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

    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }






}
