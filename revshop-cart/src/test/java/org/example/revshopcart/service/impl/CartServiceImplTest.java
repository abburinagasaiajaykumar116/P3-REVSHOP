package org.example.revshopcart.service.impl;

import feign.FeignException;
import org.example.revshopcart.client.ProductClient;
import org.example.revshopcart.client.UserClient;
import org.example.revshopcart.dto.CartItemResponse;
import org.example.revshopcart.dto.ProductDto;
import org.example.revshopcart.exception.BadRequestException;
import org.example.revshopcart.exception.ResourceNotFoundException;
import org.example.revshopcart.model.Cart;
import org.example.revshopcart.model.CartItem;
import org.example.revshopcart.repository.CartItemRepository;
import org.example.revshopcart.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart testCart;
    private CartItem testCartItem;
    private ProductDto testProduct;

    @BeforeEach
    void setUp() {
        testCart = new Cart();
        testCart.setCartId(1L);
        testCart.setUserId(1);

        testCartItem = new CartItem();
        testCartItem.setCartItemId(1L);
        testCartItem.setCartId(1L);
        testCartItem.setProductId(1L);
        testCartItem.setQuantity(2);

        testProduct = new ProductDto();
        testProduct.setProductId(1L);
        testProduct.setProductName("Test Product");
        testProduct.setPrice(100.0);
    }

    @Test
    void testAddItem_Success_NewItem() {
        when(userClient.getUserById(1)).thenReturn(ResponseEntity.ok(null));
        when(productClient.getProductById(1L)).thenReturn(testProduct);
        when(cartRepository.findByUserId(1)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.empty());

        cartService.addItem(1, 1L, 2);

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void testAddItem_Success_ExistingItem() {
        when(userClient.getUserById(1)).thenReturn(ResponseEntity.ok(null));
        when(productClient.getProductById(1L)).thenReturn(testProduct);
        when(cartRepository.findByUserId(1)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.of(testCartItem));

        cartService.addItem(1, 1L, 3);

        assertEquals(5, testCartItem.getQuantity());
        verify(cartItemRepository, times(1)).save(testCartItem);
    }

    @Test
    void testAddItem_UserNotFound_ThrowsException() {
        FeignException.NotFound feignNotFound = mock(FeignException.NotFound.class);
        when(userClient.getUserById(1)).thenThrow(feignNotFound);

        assertThrows(ResourceNotFoundException.class, () -> cartService.addItem(1, 1L, 2));
    }

    @Test
    void testViewCart_Success() {
        when(cartRepository.findByUserId(1)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartId(1L)).thenReturn(Collections.singletonList(testCartItem));
        when(productClient.getProductById(1L)).thenReturn(testProduct);

        List<CartItemResponse> result = cartService.viewCart(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getProductName());
    }

    @Test
    void testViewCart_CartNotFound_ThrowsException() {
        when(cartRepository.findByUserId(1)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cartService.viewCart(1));
    }

    @Test
    void testUpdateQuantity_Success() {
        when(userClient.getUserById(1)).thenReturn(ResponseEntity.ok(null));
        when(productClient.getProductById(1L)).thenReturn(testProduct);
        when(cartRepository.findByUserId(1)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.of(testCartItem));

        cartService.updateQuantity(1, 1L, 5);

        assertEquals(5, testCartItem.getQuantity());
        verify(cartItemRepository, times(1)).save(testCartItem);
    }

    @Test
    void testRemoveItem_Success() {
        when(userClient.getUserById(1)).thenReturn(ResponseEntity.ok(null));
        when(cartRepository.findByUserId(1)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.of(testCartItem));

        cartService.removeItem(1, 1L);

        verify(cartItemRepository, times(1)).delete(testCartItem);
    }

    @Test
    void testClearCart_Success() {
        when(userClient.getUserById(1)).thenReturn(ResponseEntity.ok(null));
        when(cartRepository.findByUserId(1)).thenReturn(Optional.of(testCart));

        cartService.clearCart(1);

        verify(cartItemRepository, times(1)).deleteByCartId(1L);
    }

}
