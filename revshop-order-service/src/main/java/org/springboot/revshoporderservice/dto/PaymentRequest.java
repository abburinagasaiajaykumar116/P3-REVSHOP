package org.springboot.revshoporderservice.dto;

import lombok.Data;

@Data
public class PaymentRequest {

    private Long orderId;
    private Double amount;
    private String paymentMethod;
    private String transactionId;
}
