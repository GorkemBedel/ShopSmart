package com.ShopSmart.ShopSmart.model;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "merchants")
public class Merchant implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String username;
    private String password;
    private String companyName;
    private String taxNumber;

    private boolean accountNonExpired;
    private boolean isEnabled;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_MERCHANT;

    @ElementCollection(targetClass = javax.management.relation.Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "all_authorities", joinColumns = @JoinColumn(name = "username", referencedColumnName = "username"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> authorities = new HashSet<>(List.of(Role.ROLE_MERCHANT));
}
