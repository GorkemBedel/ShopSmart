package com.ShopSmart.ShopSmart.service;

import com.ShopSmart.ShopSmart.dto.CreateUserRequest;
import com.ShopSmart.ShopSmart.exceptions.EmptyPasswordException;
import com.ShopSmart.ShopSmart.exceptions.UsernameNotUniqueException;
import com.ShopSmart.ShopSmart.model.Role;
import com.ShopSmart.ShopSmart.model.User;
import com.ShopSmart.ShopSmart.repository.UserRepository;
import com.ShopSmart.ShopSmart.rules.PasswordValidator;
import com.ShopSmart.ShopSmart.rules.UniqueUsernameValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UniqueUsernameValidator uniqueUsernameValidator;
    private final PasswordValidator passwordValidator;


    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                       UniqueUsernameValidator uniqueUsernameValidator, PasswordValidator passwordValidator) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.uniqueUsernameValidator = uniqueUsernameValidator;
        this.passwordValidator = passwordValidator;
    }

    public User createUser(CreateUserRequest createUserRequest) throws UsernameNotUniqueException {

        //Checking if that username is existed in database
        String username = createUserRequest.username();
        uniqueUsernameValidator.validateUsername(username);

        //Checking if the password is valid
        String password = createUserRequest.password();
        passwordValidator.validatePassword(password);



        User newUser = User.builder()
                .name(createUserRequest.name())
                .username(createUserRequest.username())
                .password(bCryptPasswordEncoder.encode(createUserRequest.password()))
                .role(Role.ROLE_USER)
                .authorities(new HashSet<>(List.of(Role.ROLE_USER)))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .isEnabled(true)
                .build();

        return userRepository.save(newUser);
    }



    public Optional<User> getByUserName(String userName){
        return userRepository.findByusername(userName);
    }





}
