package org.springboot.revshoporderservice.dto;



import lombok.Data;
import java.util.List;

@Data
public class OrderHistoryResponse {
    private Long orderId;
    private double totalAmount;
    private String status;
    private List<OrderHistoryItemDTO> items;
}
