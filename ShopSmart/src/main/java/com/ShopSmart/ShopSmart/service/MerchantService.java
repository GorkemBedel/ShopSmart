package com.ShopSmart.ShopSmart.service;

import com.ShopSmart.ShopSmart.dto.CreateMerchantRequest;
import com.ShopSmart.ShopSmart.dto.CreateUserRequest;
import com.ShopSmart.ShopSmart.dto.ProductRequest;
import com.ShopSmart.ShopSmart.model.Merchant;
import com.ShopSmart.ShopSmart.model.Product;
import com.ShopSmart.ShopSmart.model.Role;
import com.ShopSmart.ShopSmart.model.User;
import com.ShopSmart.ShopSmart.repository.MerchantRepository;
import com.ShopSmart.ShopSmart.repository.ProductRepository;
import com.ShopSmart.ShopSmart.repository.UserRepository;
import com.ShopSmart.ShopSmart.rules.PasswordValidator;
import com.ShopSmart.ShopSmart.rules.UniqueUsernameValidator;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final ProductRepository productRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UniqueUsernameValidator uniqueUsernameValidator;
    private final PasswordValidator passwordValidator;

    public MerchantService(MerchantRepository merchantRepository, ProductRepository productRepository
                        ,BCryptPasswordEncoder bCryptPasswordEncoder, UniqueUsernameValidator uniqueUsernameValidator
                        ,PasswordValidator passwordValidator) {
        this.merchantRepository = merchantRepository;
        this.productRepository = productRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.uniqueUsernameValidator = uniqueUsernameValidator;
        this.passwordValidator = passwordValidator;
    }

    public Merchant createMerchant(CreateMerchantRequest createMerchantRequest){

        //Checking if that username is exist in database
        String username = createMerchantRequest.username();
        uniqueUsernameValidator.validateUsername(username);

        //Checking if the password is valid
        String password = createMerchantRequest.password();
        passwordValidator.validatePassword(password);

        Merchant newMerchant = Merchant.builder()
                .name(createMerchantRequest.name())
                .username(createMerchantRequest.username())
                .password(bCryptPasswordEncoder.encode(createMerchantRequest.password()))
                .role(Role.ROLE_MERCHANT)
                .authorities(new HashSet<>(List.of(Role.ROLE_MERCHANT)))
                .companyName(createMerchantRequest.companyName())
                .taxNumber(createMerchantRequest.taxNumber())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .isEnabled(true)
                .build();

        return merchantRepository.save(newMerchant);
    }


    public Product addProduct(ProductRequest productRequest){
        Optional<Merchant> merchant = merchantRepository.findById(productRequest.merchantId());

        if(merchant.isEmpty()){
            throw new UsernameNotFoundException("There is no merchant with id: " + productRequest.merchantId());
        }
        System.out.println(merchant.get().getId());

        Product newProduct = Product.builder()
                .merchant(merchant.get())
                .productStock(productRequest.productStock())
                .productName(productRequest.productName())
                .build();

        Set<Product> products = merchant.get().getProducts();
        products.add(newProduct);
        merchant.get().setProducts(products);

        return productRepository.save(newProduct);
    }

    public Product deleteProduct(Long productId){

        Optional<Product> deletedProduct = productRepository.findByid(productId);
        if(deletedProduct.isPresent()) {

            //Productın sahibi olan merchant bulundu
            Merchant merchant = deletedProduct.get().getMerchant();

            //Merchantın products listesinden silindi
            Set<Product> products = merchant.getProducts();
            products.remove(deletedProduct.get());
            merchant.setProducts(products);

            //product repositoryden silindi
            productRepository.delete(deletedProduct.get());
        }
        return deletedProduct.orElseThrow(() -> new UsernameNotFoundException("There is no product with id: "+productId));


    }

    public Optional<Merchant> getByMerchantName(String merchantName){
        return merchantRepository.findByusername(merchantName);
    }




}
