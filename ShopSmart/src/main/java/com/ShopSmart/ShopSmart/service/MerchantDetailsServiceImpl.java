package com.ShopSmart.ShopSmart.service;

import com.ShopSmart.ShopSmart.model.Merchant;
import com.ShopSmart.ShopSmart.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MerchantDetailsServiceImpl implements UserDetailsService {

    private final MerchantService merchantService;

    public MerchantDetailsServiceImpl(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @Override
    public UserDetails loadUserByUsername(String merchantName) throws UsernameNotFoundException {
        Optional<Merchant> user = merchantService.getByMerchantName(merchantName);
        return user.orElseThrow(EntityNotFoundException::new);
    }
}
