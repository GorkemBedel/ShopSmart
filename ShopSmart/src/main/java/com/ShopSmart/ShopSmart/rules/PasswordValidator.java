package com.ShopSmart.ShopSmart.rules;

import com.ShopSmart.ShopSmart.exceptions.EmptyPasswordException;
import com.ShopSmart.ShopSmart.exceptions.UsernameNotUniqueException;
import com.ShopSmart.ShopSmart.model.Admin;
import com.ShopSmart.ShopSmart.model.Merchant;
import com.ShopSmart.ShopSmart.model.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PasswordValidator {


    public void validatePassword(String password) throws EmptyPasswordException {

        if (password.isEmpty()) {
            throw new EmptyPasswordException("Password can not be empty!");
        }
    }
}
