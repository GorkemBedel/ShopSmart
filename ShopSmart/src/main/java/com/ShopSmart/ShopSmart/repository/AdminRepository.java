package com.ShopSmart.ShopSmart.repository;

import com.ShopSmart.ShopSmart.model.Admin;
import com.ShopSmart.ShopSmart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByusername(String adminName);
}
