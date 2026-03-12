package com.example.revshopproductservice.dtos;


public class FavoriteView {

    private Long productId;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;

    public FavoriteView(Long productId, String name, String description, Double price, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}