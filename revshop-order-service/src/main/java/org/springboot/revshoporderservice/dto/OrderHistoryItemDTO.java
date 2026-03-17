package org.springboot.revshoporderservice.dto;



import lombok.Data;

public record OrderHistoryItemDTO(
    Long productId,
    String productName,
    Integer quantity,
    Double price
) {}
