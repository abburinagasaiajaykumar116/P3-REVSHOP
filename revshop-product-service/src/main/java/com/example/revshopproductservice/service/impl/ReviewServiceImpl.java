package com.example.revshopproductservice.service.impl;

import com.example.revshopproductservice.client.UserClient;
import com.example.revshopproductservice.dtos.ReviewView;
import com.example.revshopproductservice.dtos.SellerReviewView;
import com.example.revshopproductservice.exception.BadRequestException;
import com.example.revshopproductservice.exception.ResourceNotFoundException;
import com.example.revshopproductservice.model.Product;
import com.example.revshopproductservice.model.Review;
import com.example.revshopproductservice.repos.ProductRepository;
import com.example.revshopproductservice.repos.ReviewRepository;
import com.example.revshopproductservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserClient userClient;

    @Transactional
    @Override
    public ReviewView giveReview(Integer userId,
                                 Long productId,
                                 Integer rating,
                                 String comment) {

        if (userId == null || productId == null)
            throw new BadRequestException("User id and product id are required");

        // 🔹 Validate user from User Service
        try {
            userClient.getUser(userId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("User not found in user service");
        }

        if (reviewRepository.existsByUserIdAndProduct_ProductId(userId, productId))
            throw new BadRequestException("You have already reviewed this product");

        if (rating == null || rating < 1 || rating > 5)
            throw new BadRequestException("Rating must be between 1 and 5");

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id " + productId));

        Review review = new Review();
        review.setUserId(userId);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);

        reviewRepository.save(review);

        Double avgRating = reviewRepository.getAverageRatingByProductId(productId);
        Long reviewCount = reviewRepository.getReviewCountByProductId(productId);

        product.setAverageRating(avgRating);
        product.setReviewCount(reviewCount.intValue());

        productRepository.save(product);

        ReviewView reviewView = new ReviewView(userId, rating, comment);
        try {
            Object userObj = userClient.getUser(userId);
            if (userObj instanceof java.util.Map) {
                java.util.Map<?, ?> userMap = (java.util.Map<?, ?>) userObj;
                if (userMap.containsKey("name")) {
                    reviewView.setUserName((String) userMap.get("name"));
                }
            }
        } catch (Exception e) {
            reviewView.setUserName("Anonymous");
        }
        return reviewView;
    }

    @Override
    public List<ReviewView> viewReviewsForProduct(Long productId) {

        if (productId == null)
            throw new BadRequestException("Product id is required");

        List<ReviewView> reviews = reviewRepository.findReviewsByProduct(productId);
        for (ReviewView review : reviews) {
            try {
                Object userObj = userClient.getUser(review.getUserId());
                if (userObj instanceof java.util.Map) {
                    java.util.Map<?, ?> userMap = (java.util.Map<?, ?>) userObj;
                    if (userMap.containsKey("name")) {
                        review.setUserName((String) userMap.get("name"));
                    }
                }
            } catch (Exception e) {
                review.setUserName("Anonymous");
            }
        }
        return reviews;
    }

    @Override
    public List<SellerReviewView> viewReviewsForSeller(Integer sellerId) {

        if (sellerId == null)
            throw new BadRequestException("Seller id is required");

        List<SellerReviewView> reviews = reviewRepository.findReviewsForSeller(sellerId);
        for (SellerReviewView review : reviews) {
            try {
                Object userObj = userClient.getUser(review.getUserId());
                if (userObj instanceof java.util.Map) {
                    java.util.Map<?, ?> userMap = (java.util.Map<?, ?>) userObj;
                    if (userMap.containsKey("name")) {
                        review.setUserName((String) userMap.get("name"));
                    }
                }
            } catch (Exception e) {
                review.setUserName("Anonymous");
            }
        }
        return reviews;
    }

    @Override
    @Transactional
    public void deleteReview(Integer userId, Long productId) {

        if (userId == null || productId == null)
            throw new BadRequestException("User id and product id are required");

        // 🔹 Validate user from User Service
        try {
            userClient.getUser(userId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("User not found in user service");
        }

        Review review = reviewRepository
                .findByUserIdAndProduct_ProductId(userId, productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Review not found"));

        Product product = review.getProduct();

        reviewRepository.delete(review);

        Double avgRating = reviewRepository.getAverageRatingByProductId(productId);
        Long reviewCount = reviewRepository.getReviewCountByProductId(productId);

        if (avgRating == null)
            avgRating = 0.0;

        if (reviewCount == null)
            reviewCount = 0L;

        product.setAverageRating(avgRating);
        product.setReviewCount(reviewCount.intValue());

        productRepository.save(product);
    }
}