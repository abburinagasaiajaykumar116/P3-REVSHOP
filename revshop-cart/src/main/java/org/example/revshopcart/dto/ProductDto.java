package org.example.revshopcart.dto;

public record ProductDto(
    Long productId,
    String productName,
    Double price,
    String imageUrl
) {}
