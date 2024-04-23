package com.ShopSmart.ShopSmart.rules;

import com.ShopSmart.ShopSmart.exceptions.UsernameNotUniqueException;
import com.ShopSmart.ShopSmart.model.Admin;
import com.ShopSmart.ShopSmart.model.Merchant;
import com.ShopSmart.ShopSmart.model.User;
import com.ShopSmart.ShopSmart.repository.AdminRepository;
import com.ShopSmart.ShopSmart.repository.MerchantRepository;
import com.ShopSmart.ShopSmart.repository.UserRepository;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UniqueUsernameValidator {

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final AdminRepository adminRepository;

    public UniqueUsernameValidator(UserRepository userRepository, MerchantRepository merchantRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.adminRepository = adminRepository;
    }

    public void validateUsername(String username) throws UsernameNotUniqueException {
        Optional<User> user = userRepository.findByusername(username);
        Optional<Merchant> merchant = merchantRepository.findByusername(username);
        Optional<Admin> admin = adminRepository.findByusername(username);

        if (user.isPresent() || merchant.isPresent() || admin.isPresent()) {
            throw new UsernameNotUniqueException("That username is already taken: " + username);

        }
    }
}
