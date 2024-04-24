package com.ShopSmart.ShopSmart.service;

import com.ShopSmart.ShopSmart.model.Admin;
import com.ShopSmart.ShopSmart.model.Merchant;
import com.ShopSmart.ShopSmart.model.User;
import com.ShopSmart.ShopSmart.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;
    private final MerchantService merchantService;
    private final AdminService adminService;

    public UserDetailsServiceImpl(UserService userService, MerchantService merchantService, AdminService adminService) {
        this.userService = userService;
        this.merchantService = merchantService;
        this.adminService = adminService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userService.getByUserName(username);
        Optional<Merchant> merchant = merchantService.getByMerchantName(username);
        Optional<Admin> admin = adminService.getByAdminName(username);

        if(user.isPresent()){
            return user.get();
        }else if(merchant.isPresent()){
            return merchant.get();
        }else if(admin.isPresent()){
            return admin.get();
        }else{
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

    }
}
