package com.ShopSmart.ShopSmart.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(UsernameNotUniqueException.class)
    public ResponseEntity<?> UsernameNotUniqueExceptionHandler(UsernameNotUniqueException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyPasswordException.class)
    public ResponseEntity<?> EmptyPasswordExceptionHandler(EmptyPasswordException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NonValidPasswordException.class)
    public ResponseEntity<?> NonValidPasswordExceptionHandler(NonValidPasswordException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> UnauthorizedExceptionHandler(UnauthorizedException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

}
