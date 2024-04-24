package com.ShopSmart.ShopSmart.repository;

import com.ShopSmart.ShopSmart.model.Review;
import com.ShopSmart.ShopSmart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
