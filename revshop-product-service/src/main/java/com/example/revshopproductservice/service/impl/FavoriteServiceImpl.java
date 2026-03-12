package com.example.revshopproductservice.service.impl;

import com.example.revshopproductservice.client.UserClient;
import com.example.revshopproductservice.dtos.FavoriteView;
import com.example.revshopproductservice.exception.BadRequestException;
import com.example.revshopproductservice.exception.ResourceNotFoundException;
import com.example.revshopproductservice.model.Favorite;
import com.example.revshopproductservice.model.Product;
import com.example.revshopproductservice.repos.FavoriteRepository;
import com.example.revshopproductservice.repos.ProductRepository;
import com.example.revshopproductservice.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final UserClient userClient;

    /**
     * Validate user from User Service
     */
    private void validateUser(Integer userId) {
        try {
            userClient.getUser(userId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("User not found in user service");
        }
    }

    @Override
    public List<FavoriteView> getFavorites(Integer userId) {

        if (userId == null)
            throw new BadRequestException("User id is required");

        validateUser(userId);

        return favoriteRepository.findFavoritesByUser(userId);
    }

    @Override
    public void addFavorite(Integer userId, Long productId) {

        if (userId == null || productId == null)
            throw new BadRequestException("User id and product id are required");

        validateUser(userId);

        // Validate product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id " + productId));

        // Check duplicate favorite
        if (favoriteRepository.existsByUserIdAndProduct_ProductId(userId, productId))
            throw new BadRequestException("Product already exists in favorites");

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProduct(product);

        favoriteRepository.save(favorite);
    }

    @Transactional
    @Override
    public void removeFavorite(Integer userId, Long productId) {

        if (userId == null || productId == null)
            throw new BadRequestException("User id and product id are required");

        validateUser(userId);

        if (!favoriteRepository.existsByUserIdAndProduct_ProductId(userId, productId))
            throw new ResourceNotFoundException("Favorite not found");

        favoriteRepository.deleteByUserIdAndProduct_ProductId(userId, productId);
    }
}