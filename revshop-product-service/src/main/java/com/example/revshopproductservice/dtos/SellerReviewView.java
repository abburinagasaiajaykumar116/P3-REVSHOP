package com.example.revshopproductservice.dtos;



public record SellerReviewView(
    String productName,
    Integer userId,
    Integer rating,
    String comment,
    String userName
) {
    public SellerReviewView(String productName, Integer userId, Integer rating, String comment) {
        this(productName, userId, rating, comment, null);
    }
}