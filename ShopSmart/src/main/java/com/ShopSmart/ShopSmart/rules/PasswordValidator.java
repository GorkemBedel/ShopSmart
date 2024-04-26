package com.ShopSmart.ShopSmart.rules;

import com.ShopSmart.ShopSmart.exceptions.EmptyPasswordException;
import com.ShopSmart.ShopSmart.exceptions.NonValidPasswordException;
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
        } else if (password.length() < 8 || password.length() > 15) {
            throw new NonValidPasswordException("Your password is not valid. It's lenght should be between 8 and 15");
        }
    }
}
