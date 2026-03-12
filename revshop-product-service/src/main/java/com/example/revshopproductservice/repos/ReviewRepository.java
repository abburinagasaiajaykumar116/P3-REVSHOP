package com.example.revshopproductservice.repos;

import com.example.revshopproductservice.dtos.ReviewView;
import com.example.revshopproductservice.dtos.SellerReviewView;
import com.example.revshopproductservice.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("""
        SELECT new com.example.revshopproductservice.dtos.SellerReviewView(
            p.name,
            r.userId,
            r.rating,
            r.comment
        )
        FROM Review r
        JOIN r.product p
        WHERE p.sellerId = :sellerId
    """)
    List<SellerReviewView> findReviewsForSeller(Integer sellerId);

    @Query("""
        SELECT new com.example.revshopproductservice.dtos.ReviewView(
            r.userId,
            r.rating,
            r.comment
        )
        FROM Review r
        WHERE r.product.productId = :productId
    """)
    List<ReviewView> findReviewsByProduct(Long productId);

    @Query("""
        SELECT AVG(r.rating)
        FROM Review r
        WHERE r.product.productId = :productId
    """)
    Double getAverageRatingByProductId(Long productId);

    @Query("""
        SELECT COUNT(r)
        FROM Review r
        WHERE r.product.productId = :productId
    """)
    Long getReviewCountByProductId(Long productId);

    Optional<Review> findByUserIdAndProduct_ProductId(Integer userId, Long productId);

    boolean existsByUserIdAndProduct_ProductId(Integer userId, Long productId);
}