package org.example.revshopcart.controller;


import org.example.revshopcart.dto.CartItemResponse;
import org.example.revshopcart.dto.CartRequest;
import org.example.revshopcart.service.CartService;
import org.example.revshopcart.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtUtil jwtUtil;

    // Helper method to get the userId safely
    private Integer getUserIdOrThrow(String authHeader) {
        Integer userId = jwtUtil.extractUserIdFromHeader(authHeader);
        if (userId == null) {
            throw new RuntimeException("Unauthorized: Unable to extract User ID from token");
        }
        return userId;
    }

    // Add product to cart
    @PostMapping("/add")
    public String addToCart(@RequestHeader("Authorization") String authHeader,
                            @RequestBody CartRequest request) {

        Integer userId = getUserIdOrThrow(authHeader);
        cartService.addItem(userId, request.getProductId(), request.getQuantity());

        return "Item added to cart";
    }

    // View cart
    @GetMapping("/view")
    public List<CartItemResponse> viewCart(@RequestHeader("Authorization") String authHeader) {

        Integer userId = getUserIdOrThrow(authHeader);
        return cartService.viewCart(userId);
    }

    // Update quantity
    @PutMapping("/update")
    public String updateQuantity(@RequestHeader("Authorization") String authHeader,
                                 @RequestParam Long productId,
                                 @RequestParam int quantity) {

        Integer userId = getUserIdOrThrow(authHeader);
        cartService.updateQuantity(userId, productId, quantity);

        return "Quantity updated";
    }

    // Remove item
    @DeleteMapping("/remove")
    public String removeItem(@RequestHeader("Authorization") String authHeader,
                             @RequestParam Long productId) {

        Integer userId = getUserIdOrThrow(authHeader);
        cartService.removeItem(userId, productId);

        return "Item removed from cart";
    }

    // Clear cart
    @Transactional
    @DeleteMapping("/clear")
    public String clearCart(@RequestHeader("Authorization") String authHeader) {

        Integer userId = getUserIdOrThrow(authHeader);
        cartService.clearCart(userId);

        return "Cart cleared";
    }
}