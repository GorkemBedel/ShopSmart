package com.ShopSmart.ShopSmart.security;

import com.ShopSmart.ShopSmart.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security, HandlerMappingIntrospector introspector) throws Exception{

        MvcRequestMatcher.Builder mvcRequestBuilder = new MvcRequestMatcher.Builder(introspector);


        security
                .headers(x -> x.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
//                .csrf(csrfConfig -> csrfConfig.ignoringRequestMatchers(mvcRequestBuilder.pattern("/user/**")))
                .authorizeHttpRequests(x ->
                        x
                                .requestMatchers(mvcRequestBuilder.pattern("/ShopSmart/User/**")).hasRole(Role.ROLE_USER.getValue())
                                .requestMatchers(mvcRequestBuilder.pattern("/ShopSmart/Merchant/**")).hasRole(Role.ROLE_MERCHANT.getValue())
                                .requestMatchers(mvcRequestBuilder.pattern("/ShopSmart/Admin/**")).hasRole(Role.ROLE_ADMIN.getValue())
                                .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return security.build();
    }
}
