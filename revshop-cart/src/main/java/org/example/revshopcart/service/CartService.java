package org.example.revshopcart.service;


import org.example.revshopcart.dto.CartItemResponse;

import java.util.List;

public interface CartService {

    void addItem(Integer userId, Long productId, int quantity);

    List<CartItemResponse> viewCart(Integer userId);

    void updateQuantity(Integer userId, Long productId, int quantity);

    void removeItem(Integer userId, Long productId);

    void clearCart(Integer userId);

}