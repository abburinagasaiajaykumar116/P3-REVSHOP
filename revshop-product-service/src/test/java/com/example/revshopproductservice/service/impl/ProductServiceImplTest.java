package com.example.revshopproductservice.service.impl;

import com.example.revshopproductservice.client.NotificationClient;
import com.example.revshopproductservice.client.UserClient;
import com.example.revshopproductservice.exception.BadRequestException;
import com.example.revshopproductservice.exception.ResourceNotFoundException;
import com.example.revshopproductservice.exception.UnauthorizedException;
import com.example.revshopproductservice.model.Product;
import com.example.revshopproductservice.repos.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setProductId(1L);
        testProduct.setName("Smartphone");
        testProduct.setPrice(500.0);
        testProduct.setMrp(600.0);
        testProduct.setDiscount(10.0);
        testProduct.setSellerId(101);
        testProduct.setStockQuantity(50);
        testProduct.setStockThreshold(5);
        testProduct.setIsActive(true);
    }

    @Test
    void testAddProduct_Success() {
        when(userClient.getUser(101)).thenReturn(ResponseEntity.ok(null));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.addProduct(testProduct);

        assertNotNull(result);
        assertEquals("Smartphone", result.getName());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void testAddProduct_NullProduct_ThrowsException() {
        assertThrows(BadRequestException.class, () -> productService.addProduct(null));
    }

    @Test
    void testAddProduct_InvalidPrice_ThrowsException() {
        testProduct.setPrice(-10.0);
        when(userClient.getUser(101)).thenReturn(ResponseEntity.ok(null));
        assertThrows(BadRequestException.class, () -> productService.addProduct(testProduct));
    }

    @Test
    void testGetAllProducts_Success() {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(testProduct));
        when(productRepository.findByIsActiveTrue(any(Pageable.class))).thenReturn(productPage);
        
        Page<Product> result = productService.getAllProducts(0, 10);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        Product result = productService.getProductById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getProductId());
    }

    @Test
    void testGetProductById_NotFound_ThrowsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void testUpdateProduct_Success() {
        when(userClient.getUser(101)).thenReturn(ResponseEntity.ok(null));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product updatedProduct = new Product();
        updatedProduct.setProductId(1L);
        updatedProduct.setSellerId(101);
        updatedProduct.setName("Updated Smartphone");

        Product result = productService.updateProduct(updatedProduct);

        assertNotNull(result);
        assertEquals("Updated Smartphone", result.getName());
    }

    @Test
    void testDeleteProduct_Success() {
        when(userClient.getUser(101)).thenReturn(ResponseEntity.ok(null));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        productService.deleteProduct(1L, 101);

        verify(productRepository, times(1)).save(testProduct);
        assertFalse(testProduct.getIsActive());
    }

    @Test
    void testDeleteProduct_Unauthorized_ThrowsException() {
        when(userClient.getUser(999)).thenReturn(ResponseEntity.ok(null));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(UnauthorizedException.class, () -> productService.deleteProduct(1L, 999));
    }

    @Test
    void testReduceStock_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.reduceStock(1L, 10, "authHeader");

        assertNotNull(result);
        assertEquals(40, testProduct.getStockQuantity());
    }

    @Test
    void testReduceStock_InsufficientStock_ThrowsException() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        assertThrows(BadRequestException.class, () -> productService.reduceStock(1L, 60, "authHeader"));
    }
}
