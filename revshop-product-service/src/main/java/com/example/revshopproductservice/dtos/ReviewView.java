package com.example.revshopproductservice.dtos;


public record ReviewView(
    Integer userId,
    Integer rating,
    String comment,
    String userName
) {
    public ReviewView(Integer userId, Integer rating, String comment) {
        this(userId, rating, comment, null);
    }
}
