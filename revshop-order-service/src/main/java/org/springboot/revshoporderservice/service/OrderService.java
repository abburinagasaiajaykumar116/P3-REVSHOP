package org.springboot.revshoporderservice.service;

import org.springboot.revshoporderservice.dto.CheckoutRequest;
import org.springboot.revshoporderservice.dto.OrderHistoryResponse;
import org.springboot.revshoporderservice.dto.SellerOrderResponse;

import java.util.List;

public interface OrderService {
    Long createOrder(Long userId, CheckoutRequest request, String authHeader);
    List<OrderHistoryResponse> getOrderHistory(Long userId, String authHeader);
    List<SellerOrderResponse> getOrdersForSeller(Long sellerId, String authHeader);
    void updateOrderStatus(Long orderId, String status, String authHeader);
}