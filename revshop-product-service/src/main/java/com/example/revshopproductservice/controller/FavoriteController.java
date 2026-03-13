package com.example.revshopproductservice.controller;

import com.example.revshopproductservice.dtos.FavoriteView;
import com.example.revshopproductservice.service.FavoriteService;
import com.example.revshopproductservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final JwtUtil jwtUtil;

    private Integer getUserIdOrThrow(String authHeader) {
        Integer userId = jwtUtil.extractUserIdFromHeader(authHeader);
        if (userId == null) {
            throw new RuntimeException("Unauthorized: Unable to extract User ID from token");
        }
        return userId;
    }

    // Get user's favorite products
    @GetMapping
    public List<FavoriteView> getFavorites(@RequestHeader("Authorization") String authHeader) {
        
        Integer userId = getUserIdOrThrow(authHeader);
        return favoriteService.getFavorites(userId);
    }

    // Add product to favorites
    @PostMapping
    public String addFavorite(@RequestHeader("Authorization") String authHeader,
                              @RequestParam Long productId) {

        Integer userId = getUserIdOrThrow(authHeader);
        favoriteService.addFavorite(userId, productId);

        return "Product added to favorites";
    }

    // Remove product from favorites
    @DeleteMapping
    public String removeFavorite(@RequestHeader("Authorization") String authHeader,
                                 @RequestParam Long productId) {

        Integer userId = getUserIdOrThrow(authHeader);
        favoriteService.removeFavorite(userId, productId);

        return "Product removed from favorites";
    }
}