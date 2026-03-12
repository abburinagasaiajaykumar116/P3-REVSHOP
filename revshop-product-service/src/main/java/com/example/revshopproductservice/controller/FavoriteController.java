package com.example.revshopproductservice.controller;

import com.example.revshopproductservice.dtos.FavoriteView;
import com.example.revshopproductservice.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // Get user's favorite products
    @GetMapping("/{userId}")
    public List<FavoriteView> getFavorites(@PathVariable Integer userId) {

        return favoriteService.getFavorites(userId);
    }

    // Add product to favorites
    @PostMapping
    public String addFavorite(@RequestParam Integer userId,
                              @RequestParam Long productId) {

        favoriteService.addFavorite(userId, productId);

        return "Product added to favorites";
    }

    // Remove product from favorites
    @DeleteMapping
    public String removeFavorite(@RequestParam Integer userId,
                                 @RequestParam Long productId) {

        favoriteService.removeFavorite(userId, productId);

        return "Product removed from favorites";
    }
}