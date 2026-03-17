package com.example.revshopproductservice.dtos;


public record FavoriteView(
    Long productId,
    String name,
    String description,
    Double price,
    String imageUrl
) {}