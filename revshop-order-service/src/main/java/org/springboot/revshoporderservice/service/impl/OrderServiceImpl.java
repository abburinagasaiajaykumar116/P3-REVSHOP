package org.springboot.revshoporderservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springboot.revshoporderservice.client.NotificationClient;
import org.springboot.revshoporderservice.client.PaymentClient;
import org.springboot.revshoporderservice.client.ProductClient;
import org.springboot.revshoporderservice.client.UserClient;
import org.springboot.revshoporderservice.dto.*;
import org.springboot.revshoporderservice.exception.BadRequestException;
import org.springboot.revshoporderservice.exception.ResourceNotFoundException;
import org.springboot.revshoporderservice.exception.UnauthorizedException;
import org.springboot.revshoporderservice.model.Order;
import org.springboot.revshoporderservice.model.OrderItem;
import org.springboot.revshoporderservice.repository.OrderRepository;
import org.springboot.revshoporderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final ProductClient productClient;
    private final PaymentClient paymentClient;
    private final NotificationClient notificationClient;
    private final UserClient userClient;

    @Override
    @Transactional
    @CircuitBreaker(name = "orderService", fallbackMethod = "fallbackCreateOrder")
    @Retry(name = "orderService")
    public Long createOrder(Long userId, CheckoutRequest request, String authHeader) {
        if (userId == null) {
            throw new UnauthorizedException("User must be logged in to place an order.");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Order cannot be processed without items.");
        }

        if (request.getShippingAddress() == null || request.getShippingAddress().isBlank()) {
            throw new BadRequestException("Shipping address is required.");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setStatus("PLACED");

        double totalAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();
        Set<Long> sellerIds = new HashSet<>();

        for (var itemReq : request.getItems()) {
            try {
                productClient.reduceStock(itemReq.getProductId(), itemReq.getQuantity(), authHeader);
                
                ProductDTO productDTO = productClient.getProductById(itemReq.getProductId(), authHeader);
                if (productDTO != null && productDTO.getSellerId() != null) {
                    sellerIds.add(productDTO.getSellerId());
                }
            } catch (Exception e) {
                System.out.println("Product Service unreachable for Product ID: " + itemReq.getProductId());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(itemReq.getProductId());
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(itemReq.getPrice());

            totalAmount += (itemReq.getPrice() * itemReq.getQuantity());
            orderItems.add(item);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);
        order = orderRepo.save(order);
        
        // --- PAYMENT INTEGRATION ---
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(order.getOrderId());
        paymentRequest.setAmount(totalAmount);
        paymentRequest.setPaymentMethod(request.getPaymentMethod()); 
        paymentRequest.setTransactionId(request.getTransactionId());

        try {
            PaymentResponse paymentResponse = paymentClient.makePayment(authHeader, paymentRequest);
            if ("SUCCESS".equalsIgnoreCase(paymentResponse.getStatus())) {
                order.setStatus("PLACED");
                order.setPaymentMethod(paymentResponse.getPaymentMethod());
                
                // NOTIFY BUYER
                try {
                    notificationClient.sendNotification(authHeader, userId.intValue(), "Order created successfully with Order No #" + order.getOrderId(), "ORDER_PLACED");
                } catch (Exception e) {
                    System.out.println("Failed to notify buyer: " + e.getMessage());
                }
                
                // NOTIFY SELLERS
                for (Long sellerId : sellerIds) {
                    try {
                        notificationClient.sendNotification(authHeader, sellerId.intValue(), "Order received. Order No #" + order.getOrderId() + " contains your products.", "ORDER_RECEIVED");
                    } catch (Exception e) {
                        System.out.println("Failed to notify seller " + sellerId + ": " + e.getMessage());
                    }
                }
                
            } else {
                order.setStatus("PAYMENT_FAILED");
            }
        } catch (Exception e) {
            order.setStatus("PAYMENT_PENDING");
            System.out.println("Payment service failed or unreachable: " + e.getMessage());
        }
        
        return orderRepo.save(order).getOrderId();
    }

    public Long fallbackCreateOrder(Long userId, CheckoutRequest request, String authHeader, Throwable t) {
        log.error("Fallback triggered for createOrder due to: {}", t.getMessage());
        throw new BadRequestException("Order service is currently under high load or dependent services are down. Please try again later.");
    }

    @Override
    @CircuitBreaker(name = "orderService", fallbackMethod = "fallbackGetOrderHistory")
    @Retry(name = "orderService")
    public List<OrderHistoryResponse> getOrderHistory(Long userId, String authHeader) {
        List<Order> orders = orderRepo.findByUserId(userId);

        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No order history found for User ID: " + userId);
        }

        return orders.stream().map(order -> {
            OrderHistoryResponse res = new OrderHistoryResponse();
            res.setOrderId(order.getOrderId());
            res.setTotalAmount(order.getTotalAmount());
            res.setStatus(order.getStatus());

            List<OrderHistoryItemDTO> itemDTOs = order.getOrderItems().stream().map(item -> {
                OrderHistoryItemDTO itemDTO = new OrderHistoryItemDTO();
                itemDTO.setProductId(item.getProductId());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPrice(item.getPrice());

                try {
                    ProductDTO product = productClient.getProductById(item.getProductId(), authHeader);
                    itemDTO.setProductName(product.getName());
                } catch (Exception e) {
                    itemDTO.setProductName("Product details unavailable");
                }
                return itemDTO;
            }).toList();

            res.setItems(itemDTOs);
            return res;
        }).toList();
    }

    public List<OrderHistoryResponse> fallbackGetOrderHistory(Long userId, String authHeader, Throwable t) {
        log.error("Fallback triggered for getOrderHistory due to: {}", t.getMessage());
        return java.util.Collections.emptyList();
    }

    @Override
    @CircuitBreaker(name = "orderService", fallbackMethod = "fallbackGetOrdersForSeller")
    @Retry(name = "orderService")
    public List<SellerOrderResponse> getOrdersForSeller(Long sellerId, String authHeader) {
        if (sellerId == null) {
            throw new UnauthorizedException("Seller ID is required.");
        }

        // 1. Get real Product IDs from Product Service via Feign Client
        List<Long> sellerProductIds = productClient.getProductIdsBySeller(sellerId, authHeader);

        if (sellerProductIds == null || sellerProductIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        // 2. Fetch orders containing these products using the Repository manual query
        List<Order> orders = orderRepo.findOrdersByProductIds(sellerProductIds);

        if (orders == null || orders.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        return orders.stream().flatMap(order -> order.getOrderItems().stream()
                .filter(item -> sellerProductIds.contains(item.getProductId()))
                .map(item -> {
                    SellerOrderResponse res = new SellerOrderResponse();
                    res.setOrderId(order.getOrderId());
                    res.setProductId(item.getProductId());
                    res.setQuantity(item.getQuantity());
                    res.setPrice(item.getPrice());
                    res.setStatus(order.getStatus());
                    res.setShippingAddress(order.getShippingAddress());
                    res.setBillingAddress(order.getBillingAddress());
                    res.setPaymentMethod(order.getPaymentMethod());

                    // Enrichment: Fetch Customer Name
                    try {
                        Map<String, Object> user = userClient.getUserById(order.getUserId());
                        if (user != null && user.containsKey("name")) {
                            res.setCustomerName((String) user.get("name"));
                        } else {
                            res.setCustomerName("Unknown Customer");
                        }
                    } catch (Exception e) {
                        res.setCustomerName("Customer Name Unavailable");
                    }

                    // 3. ENRICHMENT: Fetch the product name for the seller view
                    try {
                        ProductDTO product = productClient.getProductById(item.getProductId(), authHeader);
                        res.setProductName(product.getName());
                    } catch (Exception e) {
                        res.setProductName("Name Unavailable");
                    }

                    return res;
                })).toList();
    }

    public List<SellerOrderResponse> fallbackGetOrdersForSeller(Long sellerId, String authHeader, Throwable t) {
        log.error("Fallback triggered for getOrdersForSeller due to: {}", t.getMessage());
        return java.util.Collections.emptyList();
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, String status, String authHeader) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order ID " + orderId + " not found."));

        if (status == null || status.isBlank()) {
            throw new BadRequestException("Status update cannot be empty.");
        }

        order.setStatus(status.toUpperCase());
        orderRepo.save(order);

        // NOTIFY BUYER ON STATUS CHANGE
        try {
            notificationClient.sendNotification(authHeader != null ? authHeader : "",
                    order.getUserId().intValue(),
                    "Your order #" + order.getOrderId() + " status has been updated to " + order.getStatus(),
                    "ORDER_STATUS_UPDATE");
        } catch (Exception e) {
            log.error("Failed to notify buyer about status change: {}", e.getMessage());
        }
    }
}