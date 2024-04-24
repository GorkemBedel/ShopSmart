package com.ShopSmart.ShopSmart.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private Long productStock;

    private Set<String> reviews;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "merchantId", nullable = false)
    private Merchant merchant;




}
