package com.ShopSmart.ShopSmart.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UsernameNotUniqueException extends RuntimeException {

    public UsernameNotUniqueException(String message) {
        super(message);
    }
}