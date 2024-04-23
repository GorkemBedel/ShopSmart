package com.ShopSmart.ShopSmart.service;

import com.ShopSmart.ShopSmart.dto.CreateMerchantRequest;
import com.ShopSmart.ShopSmart.dto.CreateUserRequest;
import com.ShopSmart.ShopSmart.model.Merchant;
import com.ShopSmart.ShopSmart.model.Role;
import com.ShopSmart.ShopSmart.model.User;
import com.ShopSmart.ShopSmart.repository.MerchantRepository;
import com.ShopSmart.ShopSmart.repository.UserRepository;
import com.ShopSmart.ShopSmart.rules.UniqueUsernameValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UniqueUsernameValidator uniqueUsernameValidator;

    public MerchantService(MerchantRepository merchantRepository, BCryptPasswordEncoder bCryptPasswordEncoder, UniqueUsernameValidator uniqueUsernameValidator) {
        this.merchantRepository = merchantRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.uniqueUsernameValidator = uniqueUsernameValidator;
    }

    public Merchant createMerchant(CreateMerchantRequest createMerchantRequest){

        //Checking if that username is exists in database
        String username = createMerchantRequest.username();
        uniqueUsernameValidator.validateUsername(username);

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

    public Optional<Merchant> getByMerchantName(String merchantName){
        return merchantRepository.findByusername(merchantName);
    }




}
