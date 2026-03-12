package com.example.revshopproductservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer rating;
    private String comment;

    public Review() {}

    public Review(Integer userId, Product product, Integer rating, String comment) {
        this.userId = userId;
        this.product = product;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getReviewId() { return reviewId; }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getUserId() { return userId; }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Product getProduct() { return product; }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getRating() { return rating; }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() { return comment; }

    public void setComment(String comment) {
        this.comment = comment;
    }
}