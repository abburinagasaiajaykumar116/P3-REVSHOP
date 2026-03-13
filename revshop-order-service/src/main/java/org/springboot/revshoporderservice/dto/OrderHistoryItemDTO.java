package org.springboot.revshoporderservice.dto;



import lombok.Data;

@Data
public class OrderHistoryItemDTO {
    private Long productId;
    private String productName; // Fetched from Product Service
    private Integer quantity;
    private Double price;
}
