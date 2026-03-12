package com.example.revshopproductservice.service;

import com.example.revshopproductservice.dtos.FavoriteView;

import java.util.List;

public interface FavoriteService {

    List<FavoriteView> getFavorites(Integer userId);

    void addFavorite(Integer userId, Long productId);

    void removeFavorite(Integer userId, Long productId);
}