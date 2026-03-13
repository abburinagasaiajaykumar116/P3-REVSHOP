package org.example.revshopcart.service.impl;


import org.example.revshopcart.dto.CartItemResponse;
import org.example.revshopcart.exception.BadRequestException;
import org.example.revshopcart.exception.ResourceNotFoundException;
import org.example.revshopcart.model.Cart;
import org.example.revshopcart.model.CartItem;
import org.example.revshopcart.repository.CartItemRepository;
import org.example.revshopcart.repository.CartRepository;
import org.example.revshopcart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.example.revshopcart.client.ProductClient;
import org.example.revshopcart.client.UserClient;
import feign.FeignException;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ProductClient productClient;

    private Cart getOrCreateCart(Integer userId) {

        if (userId == null)
            throw new BadRequestException("User id is required");

        try {
            userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("User not found with id " + userId);
        }

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public void addItem(Integer userId, Long productId, int quantity) {

        if (productId == null)
            throw new BadRequestException("Product id is required");

        if (quantity <= 0)
            throw new BadRequestException("Quantity must be greater than 0");

        try {
            productClient.getProductById(productId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Product not found with id " + productId);
        }

        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getCartId(), productId)
                .orElse(new CartItem());

        item.setCartId(cart.getCartId());
        item.setProductId(productId);
        item.setQuantity(item.getQuantity() + quantity);

        cartItemRepository.save(item);
    }

    @Override
    public List<CartItemResponse> viewCart(Integer userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found for user " + userId));

        List<CartItem> items = cartItemRepository.findByCartId(cart.getCartId());

        return items.stream()
                .map(item -> {
                    CartItemResponse response = new CartItemResponse(
                            item.getCartItemId(),
                            item.getProductId(),
                            item.getQuantity()
                    );
                    try {
                        org.example.revshopcart.dto.ProductDto product = productClient.getProductById(item.getProductId());
                        if (product != null) {
                            response.setProductName(product.getProductName());
                            response.setPrice(product.getPrice());
                            response.setImageUrl(product.getImageUrl());
                        }
                    } catch (Exception e) {
                        response.setProductName("Product Unavailable");
                        response.setPrice(0.0);
                    }
                    return response;
                })
                .toList();
    }

    @Override
    public void updateQuantity(Integer userId, Long productId, int quantity) {

        if (quantity <= 0)
            throw new BadRequestException("Quantity must be greater than 0");

        try {
            productClient.getProductById(productId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Product not found with id " + productId);
        }

        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getCartId(), productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Item not found in cart"));

        item.setQuantity(quantity);

        cartItemRepository.save(item);
    }

    @Override
    public void removeItem(Integer userId, Long productId) {

        Cart cart = getOrCreateCart(userId);

        cartItemRepository
                .findByCartIdAndProductId(cart.getCartId(), productId)
                .ifPresent(cartItemRepository::delete);
    }

    @Override
    public void clearCart(Integer userId) {

        Cart cart = getOrCreateCart(userId);

        cartItemRepository.deleteByCartId(cart.getCartId());
    }
}