package org.springboot.revshoporderservice.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Long productId;
    private String name;
    private Double price;
    private Long sellerId; // Important for seller features later
}
