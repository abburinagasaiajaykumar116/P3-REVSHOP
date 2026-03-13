package com.example.revshopproductservice.controller;

import com.example.revshopproductservice.dtos.ReviewView;
import com.example.revshopproductservice.dtos.SellerReviewView;
import com.example.revshopproductservice.service.ReviewService;
import com.example.revshopproductservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtUtil jwtUtil;

    private Integer getUserIdOrThrow(String authHeader) {
        Integer userId = jwtUtil.extractUserIdFromHeader(authHeader);
        if (userId == null) {
            throw new RuntimeException("Unauthorized: Unable to extract User ID from token");
        }
        return userId;
    }

    // Add review
    @PostMapping
    public ReviewView giveReview(@RequestHeader("Authorization") String authHeader,
                                 @RequestParam Long productId,
                                 @RequestParam Integer rating,
                                 @RequestParam(required = false) String comment) {

        Integer userId = getUserIdOrThrow(authHeader);
        return reviewService.giveReview(userId, productId, rating, comment);
    }

    // View reviews for a product
    @GetMapping("/product/{productId}")
    public List<ReviewView> viewReviewsForProduct(@PathVariable Long productId) {

        return reviewService.viewReviewsForProduct(productId);
    }

    // Seller dashboard reviews
    @GetMapping("/seller")
    public List<SellerReviewView> viewReviewsForSeller(@RequestHeader("Authorization") String authHeader) {

        Integer sellerId = getUserIdOrThrow(authHeader);
        return reviewService.viewReviewsForSeller(sellerId);
    }

    //Delete Review
    @DeleteMapping
    public String deleteReview(@RequestHeader("Authorization") String authHeader,
                               @RequestParam Long productId) {

        Integer userId = getUserIdOrThrow(authHeader);
        reviewService.deleteReview(userId, productId);

        return "Review deleted successfully";
    }
}