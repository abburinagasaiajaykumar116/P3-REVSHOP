package com.example.revshopproductservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "seller_id", nullable = false)
    private Integer sellerId;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    private Double price;
    private Double mrp;
    private Double discount;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "stock_threshold")
    private Integer stockThreshold;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    public Product() {}

    public Product(Long productId, Integer sellerId, Integer categoryId, String name,
                   String description, Double price, Double mrp, Double discount,
                   Integer stockQuantity, Integer stockThreshold, Boolean isActive,
                   String imageUrl, LocalDateTime createdAt) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.mrp = mrp;
        this.discount = discount;
        this.stockQuantity = stockQuantity;
        this.stockThreshold = stockThreshold;
        this.isActive = isActive;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getSellerId() { return sellerId; }
    public void setSellerId(Integer sellerId) { this.sellerId = sellerId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getMrp() { return mrp; }
    public void setMrp(Double mrp) { this.mrp = mrp; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Integer getStockThreshold() { return stockThreshold; }
    public void setStockThreshold(Integer stockThreshold) { this.stockThreshold = stockThreshold; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Double getAverageRating() {return averageRating;}
    public void setAverageRating(Double averageRating) {this.averageRating = averageRating;}

    public Integer getReviewCount() {return reviewCount;}
    public void setReviewCount(Integer reviewCount) {this.reviewCount = reviewCount;}
}