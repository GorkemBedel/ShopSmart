package com.ShopSmart.ShopSmart.repository;

import com.ShopSmart.ShopSmart.model.Admin;
import com.ShopSmart.ShopSmart.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByid(Long ProductId);
}
