package com.example.revshopproductservice.service;

import com.example.revshopproductservice.dtos.ReviewView;
import com.example.revshopproductservice.dtos.SellerReviewView;

import java.util.List;

public interface ReviewService {

    ReviewView giveReview(Integer userId,
                          Long productId,
                          Integer rating,
                          String comment);

    List<ReviewView> viewReviewsForProduct(Long productId);

    List<SellerReviewView> viewReviewsForSeller(Integer sellerId);

    void deleteReview(Integer userId, Long productId);
}