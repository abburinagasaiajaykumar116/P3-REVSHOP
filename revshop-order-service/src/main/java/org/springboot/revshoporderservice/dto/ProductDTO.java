package org.springboot.revshoporderservice.dto;

import lombok.Data;

public record ProductDTO(
    Long productId,
    String name,
    Double price,
    Long sellerId
) {}
