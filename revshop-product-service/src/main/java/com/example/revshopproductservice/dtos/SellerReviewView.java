package com.example.revshopproductservice.dtos;



public class SellerReviewView {

    private String productName;
    private Integer userId;
    private Integer rating;
    private String comment;
    private String userName;

    public SellerReviewView(String productName, Integer userId, Integer rating, String comment) {
        this.productName = productName;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    public String getProductName() { return productName; }
    public Integer getUserId() { return userId; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}