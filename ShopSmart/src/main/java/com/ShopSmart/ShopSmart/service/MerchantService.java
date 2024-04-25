package com.ShopSmart.ShopSmart.service;

import com.ShopSmart.ShopSmart.dto.CreateMerchantRequest;
import com.ShopSmart.ShopSmart.dto.CreateUserRequest;
import com.ShopSmart.ShopSmart.dto.ProductRequest;
import com.ShopSmart.ShopSmart.exceptions.UnauthorizedException;
import com.ShopSmart.ShopSmart.exceptions.UsernameNotUniqueException;
import com.ShopSmart.ShopSmart.model.*;
import com.ShopSmart.ShopSmart.repository.MerchantRepository;
import com.ShopSmart.ShopSmart.repository.ProductRepository;
import com.ShopSmart.ShopSmart.repository.ReviewRepository;
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
    private final ReviewRepository reviewRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UniqueUsernameValidator uniqueUsernameValidator;
    private final PasswordValidator passwordValidator;

    public MerchantService(MerchantRepository merchantRepository, ProductRepository productRepository
                        ,BCryptPasswordEncoder bCryptPasswordEncoder, UniqueUsernameValidator uniqueUsernameValidator
                        ,PasswordValidator passwordValidator, ReviewRepository reviewRepository) {
        this.merchantRepository = merchantRepository;
        this.productRepository = productRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.uniqueUsernameValidator = uniqueUsernameValidator;
        this.passwordValidator = passwordValidator;
        this.reviewRepository = reviewRepository;
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

        if(productRequest.productPrice() <= 0){
            throw new UsernameNotFoundException("Price should be more then 0!");
        }

        if(merchant.isPresent()) {
            Product newProduct = Product.builder()
                    .merchant(merchant.get())
                    .productStock(productRequest.productStock())
                    .productName(productRequest.productName())
                    .description(productRequest.productDescription())
                    .productPrice(productRequest.productPrice())
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

        if(updateRequest.productPrice() <= 0){
            throw new UsernameNotUniqueException("Price should be more then 0!");
        }

        if(updatedProduct.isPresent()){
            // if updated product's merchant is not the merchant who is logged in, throw exception
            if(!updatedProduct.get().getMerchant().getUsername().equals(loggedInMerchantUsername)){
                throw new UnauthorizedException("You are trying to update another merchant's product.");

            }
            updatedProduct.get().setProductName(updateRequest.productName());
            updatedProduct.get().setProductStock(updateRequest.productStock());
            updatedProduct.get().setDescription(updateRequest.productDescription());
            updatedProduct.get().setProductPrice(updateRequest.productPrice());
            return productRepository.save(updatedProduct.get());
        } else {
            throw new UsernameNotUniqueException("Product Id not found");
        }

    }

    public Merchant deleteMerchant(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();

        Optional<Merchant> merchantOptional = merchantRepository.findByusername(loggedInUsername);
        if (merchantOptional.isPresent()) {
            Merchant deletedMerchant = merchantOptional.get();
            merchantRepository.delete(deletedMerchant);
            return deletedMerchant;
        } else {
            throw new UsernameNotFoundException("Deleted merchant can not be found");
        }

    }


    public Review deleteReview(Long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();

        Optional<Merchant> merchantOptional = merchantRepository.findByusername(loggedInUsername);
        Optional<Review> reviewOptional = reviewRepository.findById(id);

        if (merchantOptional.isPresent() && reviewOptional.isPresent()) {

            //review yapılan ürünün sahibi olan merchant ile logged in olan merchant aynı mı onu karşılaştırıyoruz
            Merchant ownerOfTheReviewedProduct = reviewOptional.get().getProduct().getMerchant();
            if(!ownerOfTheReviewedProduct.getUsername().equals(loggedInUsername)){
                throw new UsernameNotUniqueException("You are trying to delete another merchant's review!");
            }
            reviewRepository.delete(reviewOptional.get());
            return reviewOptional.get();

        } else {
            throw new UsernameNotUniqueException("There is no review with id: "+ id);
        }
    }
}
