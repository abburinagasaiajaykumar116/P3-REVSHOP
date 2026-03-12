package com.example.revshopproductservice.controller;

import com.example.revshopproductservice.dtos.ReviewView;
import com.example.revshopproductservice.dtos.SellerReviewView;
import com.example.revshopproductservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Add review
    @PostMapping
    public ReviewView giveReview(@RequestParam Integer userId,
                                 @RequestParam Long productId,
                                 @RequestParam Integer rating,
                                 @RequestParam(required = false) String comment) {

        return reviewService.giveReview(userId, productId, rating, comment);
    }

    // View reviews for a product
    @GetMapping("/product/{productId}")
    public List<ReviewView> viewReviewsForProduct(@PathVariable Long productId) {

        return reviewService.viewReviewsForProduct(productId);
    }

    // Seller dashboard reviews
    @GetMapping("/seller/{sellerId}")
    public List<SellerReviewView> viewReviewsForSeller(@PathVariable Integer sellerId) {

        return reviewService.viewReviewsForSeller(sellerId);
    }

    //Delete Review
    @DeleteMapping
    public String deleteReview(@RequestParam Integer userId,
                               @RequestParam Long productId) {

        reviewService.deleteReview(userId, productId);

        return "Review deleted successfully";
    }
}