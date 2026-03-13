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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Product testProduct;
    private ReviewView testReviewView;



    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setProductId(1L);

        testReviewView = new ReviewView(1, 5, "Great product");
    }

    @Test
    void testGiveReview_Success() {
        when(userClient.getUser(1)).thenReturn(ResponseEntity.ok(null));
        when(reviewRepository.existsByUserIdAndProduct_ProductId(1, 1L)).thenReturn(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.save(any(Review.class))).thenReturn(new Review());
        
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(5.0);
        when(reviewRepository.getReviewCountByProductId(1L)).thenReturn(1L);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", "John Doe");
        when(userClient.getUser(1)).thenReturn(userMap);

        ReviewView result = reviewService.giveReview(1, 1L, 5, "Great product");

        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("John Doe", result.getUserName());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void testGiveReview_DuplicateReview_ThrowsException() {
        when(userClient.getUser(1)).thenReturn(ResponseEntity.ok(null));
        when(reviewRepository.existsByUserIdAndProduct_ProductId(1, 1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> reviewService.giveReview(1, 1L, 5, "Great product"));
    }

    @Test
    void testGiveReview_InvalidRating_ThrowsException() {
        when(userClient.getUser(1)).thenReturn(ResponseEntity.ok(null));
        when(reviewRepository.existsByUserIdAndProduct_ProductId(1, 1L)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> reviewService.giveReview(1, 1L, 6, "Great product"));
    }

    @Test
    void testViewReviewsForProduct_Success() {
        when(reviewRepository.findReviewsByProduct(1L)).thenReturn(Collections.singletonList(testReviewView));

        List<ReviewView> result = reviewService.viewReviewsForProduct(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testDeleteReview_Success() {
        when(userClient.getUser(1)).thenReturn(ResponseEntity.ok(null));
        
        Review review = new Review();
        review.setProduct(testProduct);
        when(reviewRepository.findByUserIdAndProduct_ProductId(1, 1L)).thenReturn(Optional.of(review));

        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(0.0);
        when(reviewRepository.getReviewCountByProductId(1L)).thenReturn(0L);

        reviewService.deleteReview(1, 1L);

        verify(reviewRepository, times(1)).delete(review);
        verify(productRepository, times(1)).save(testProduct);
    }
}
