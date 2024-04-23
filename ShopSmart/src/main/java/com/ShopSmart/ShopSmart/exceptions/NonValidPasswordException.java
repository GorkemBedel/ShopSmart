package com.ShopSmart.ShopSmart.exceptions;

public class NonValidPasswordException extends RuntimeException{

    public NonValidPasswordException(String message) {
        super(message);
    }
}
