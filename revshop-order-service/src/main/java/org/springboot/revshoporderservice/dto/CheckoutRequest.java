package org.springboot.revshoporderservice.dto;



import lombok.Data;
import java.util.List;

@Data
public class CheckoutRequest {
    private String shippingAddress;
    private String billingAddress;
    private String paymentMethod;
    private String transactionId;
    private List<OrderItemRequest> items; // Items being bought
}
