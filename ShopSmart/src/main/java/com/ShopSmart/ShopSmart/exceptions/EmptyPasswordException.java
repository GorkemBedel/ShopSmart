package com.ShopSmart.ShopSmart.exceptions;

public class EmptyPasswordException extends RuntimeException {

    public EmptyPasswordException(String message) {
        super(message);
    }
}