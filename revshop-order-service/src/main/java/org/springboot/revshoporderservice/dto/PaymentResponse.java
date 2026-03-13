package org.springboot.revshoporderservice.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private String transactionId;
    private String status;
    private String message;
    private String paymentMethod;
}
