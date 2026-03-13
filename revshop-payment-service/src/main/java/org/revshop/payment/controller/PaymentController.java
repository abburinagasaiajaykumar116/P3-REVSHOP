package org.revshop.payment.controller;

import lombok.RequiredArgsConstructor;
import org.revshop.payment.dto.PaymentRequest;
import org.revshop.payment.dto.PaymentResponse;
import org.revshop.payment.service.PaymentService;
import org.revshop.payment.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> makePayment(@RequestHeader("Authorization") String authHeader,
                                         @RequestBody PaymentRequest request) {
                                         
        Integer userId = jwtUtil.extractUserIdFromHeader(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized: Invalid or missing token"));
        }

        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/razorpay-order")
    public ResponseEntity<?> createRazorpayOrder(@RequestHeader("Authorization") String authHeader,
                                                 @RequestBody Map<String, Double> payload) {
        Integer userId = jwtUtil.extractUserIdFromHeader(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized: Invalid or missing token"));
        }

        Double amount = payload.get("amount");
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or missing amount"));
        }

        try {
            String razorpayOrderId = paymentService.createRazorpayOrder(amount);
            return ResponseEntity.ok(Map.of("razorpayOrderId", razorpayOrderId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}