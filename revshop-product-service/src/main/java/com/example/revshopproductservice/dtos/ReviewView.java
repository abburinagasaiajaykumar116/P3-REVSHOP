package com.example.revshopproductservice.dtos;


public class ReviewView {

    private Integer userId;
    private Integer rating;
    private String comment;

    public ReviewView(Integer userId, Integer rating, String comment) {
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    public Integer getUserId() { return userId; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
}
