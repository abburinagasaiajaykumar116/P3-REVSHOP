package org.springboot.revshoporderservice.controller;

import lombok.RequiredArgsConstructor;
import org.springboot.revshoporderservice.dto.CheckoutRequest;
import org.springboot.revshoporderservice.dto.OrderHistoryResponse;
import org.springboot.revshoporderservice.dto.SellerOrderResponse;
import org.springboot.revshoporderservice.service.OrderService;
import org.springboot.revshoporderservice.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    private Long getUserIdOrThrow(String authHeader) {
        Integer userId = jwtUtil.extractUserIdFromHeader(authHeader);
        if (userId == null) {
            throw new RuntimeException("Unauthorized: Invalid token");
        }
        return userId.longValue();
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestHeader("Authorization") String authHeader,
                                      @RequestBody CheckoutRequest req) {
        try {
            Long userId = getUserIdOrThrow(authHeader);
            Long id = orderService.createOrder(userId, req, authHeader);
            return ResponseEntity.ok(Map.of("orderId", id, "status", "Success", "message", "Your order placed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = getUserIdOrThrow(authHeader);
            List<OrderHistoryResponse> history = orderService.getOrderHistory(userId, authHeader);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/seller")
    public ResponseEntity<?> getSellerOrders(@RequestHeader("Authorization") String authHeader) {
        try {
            Long sellerId = getUserIdOrThrow(authHeader);
            return ResponseEntity.ok(orderService.getOrdersForSeller(sellerId, authHeader));
        } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long orderId, @RequestParam String status, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        orderService.updateOrderStatus(orderId, status, authHeader);
        return ResponseEntity.ok(Map.of("message", "Status updated to " + status));
    }
}
