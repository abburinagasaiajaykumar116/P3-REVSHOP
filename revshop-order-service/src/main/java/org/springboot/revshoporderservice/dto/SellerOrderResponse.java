package org.springboot.revshoporderservice.dto;


import lombok.Data;

@Data
public class SellerOrderResponse {
    private Long orderId;
    private Long productId;
    private String productName;
    private String customerName;
    private Integer quantity;
    private Double price;
    private String status;
    private String shippingAddress;
    private String billingAddress;
    private String paymentMethod;
}
