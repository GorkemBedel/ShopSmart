package com.ShopSmart.ShopSmart.service;

import com.ShopSmart.ShopSmart.dto.CreateMerchantRequest;
import com.ShopSmart.ShopSmart.dto.CreateUserRequest;
import com.ShopSmart.ShopSmart.dto.ProductRequest;
import com.ShopSmart.ShopSmart.exceptions.UnauthorizedException;
import com.ShopSmart.ShopSmart.model.Merchant;
import com.ShopSmart.ShopSmart.model.Product;
import com.ShopSmart.ShopSmart.model.Role;
import com.ShopSmart.ShopSmart.model.User;
import com.ShopSmart.ShopSmart.repository.MerchantRepository;
import com.ShopSmart.ShopSmart.repository.ProductRepository;
import com.ShopSmart.ShopSmart.repository.UserRepository;
import com.ShopSmart.ShopSmart.rules.PasswordValidator;
import com.ShopSmart.ShopSmart.rules.UniqueUsernameValidator;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.*;
import java.util.List;

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInMerchantUsername = authentication.getName();

        //find current logged in merchant
        Optional<Merchant> merchant = merchantRepository.findByusername(loggedInMerchantUsername);

        if(merchant.isPresent()) {
            Product newProduct = Product.builder()
                    .merchant(merchant.get())
                    .productStock(productRequest.productStock())
                    .productName(productRequest.productName())
                    .description(productRequest.productDescription())
                    .build();

            Set<Product> products = merchant.get().getProducts();
            products.add(newProduct);
            merchant.get().setProducts(products);
            return productRepository.save(newProduct);
        }else{
            throw new UsernameNotFoundException("Merchant is not exist");
        }

    }

    public Product deleteProduct(Long productId){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInMerchantUsername = authentication.getName();

        Optional<Product> deletedProduct = productRepository.findByid(productId);
        if(deletedProduct.isPresent()) {

            //Productın sahibi olan merchant bulundu
            Merchant merchant = deletedProduct.get().getMerchant();
            if(!merchant.getUsername().equals(loggedInMerchantUsername)){
                throw new UnauthorizedException("You are trying to delete another merchant's product.");
            }

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

    public List<Product> getAllProducts(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInMerchantUsername = authentication.getName();

        //All products in database
        List<Product> productList = productRepository.findAll();

        //Just given merchants products
        List<Product> merchantsProducts = new ArrayList<>();
        for(Product p: productList){
            if(p.getMerchant().getUsername().equals(loggedInMerchantUsername)){
                merchantsProducts.add(p);
            }
        }

        return merchantsProducts;

    }

    public Product updateProduct(Long productId, ProductRequest updateRequest){
        Optional<Product> updatedProduct = productRepository.findByid(productId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInMerchantUsername = authentication.getName();

        if(updatedProduct.isPresent()){
            // if updated products merchant is not the merchant who is logged in, throw exception
            if(!updatedProduct.get().getMerchant().getUsername().equals(loggedInMerchantUsername)){
                throw new UnauthorizedException("You are trying to update another merchant's product.");

            }
            updatedProduct.get().setProductName(updateRequest.productName());
            updatedProduct.get().setProductStock(updateRequest.productStock());
            updatedProduct.get().setDescription(updateRequest.productDescription());
            return productRepository.save(updatedProduct.get());
        } else {
            throw new UsernameNotFoundException("Product Id not found");
        }

    }






}
