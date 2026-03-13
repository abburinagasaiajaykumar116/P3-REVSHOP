package com.example.revshopproductservice.service.impl;

import com.example.revshopproductservice.client.UserClient;
import com.example.revshopproductservice.dtos.FavoriteView;
import com.example.revshopproductservice.exception.BadRequestException;
import com.example.revshopproductservice.exception.ResourceNotFoundException;
import com.example.revshopproductservice.model.Favorite;
import com.example.revshopproductservice.model.Product;
import com.example.revshopproductservice.repos.FavoriteRepository;
import com.example.revshopproductservice.repos.ProductRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceImplTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private Product testProduct;
    private FavoriteView testFavoriteView;


    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setProductId(1L);

        testFavoriteView = new FavoriteView(1L, "Test Product", "Test Desc", 100.0, "url");
    }

    @Test
    void testGetFavorites_Success() {
        when(userClient.getUser(1)).thenReturn(ResponseEntity.ok(null));
        when(favoriteRepository.findFavoritesByUser(1)).thenReturn(Collections.singletonList(testFavoriteView));

        List<FavoriteView> result = favoriteService.getFavorites(1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetFavorites_NullUserId_ThrowsException() {
        assertThrows(BadRequestException.class, () -> favoriteService.getFavorites(null));
    }

    @Test
    void testGetFavorites_UserNotFound_ThrowsException() {
        when(userClient.getUser(1)).thenThrow(new RuntimeException("User not found"));
        assertThrows(ResourceNotFoundException.class, () -> favoriteService.getFavorites(1));
    }

    @Test
    void testAddFavorite_Success() {
        when(userClient.getUser(1)).thenReturn(ResponseEntity.ok(null));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(favoriteRepository.existsByUserIdAndProduct_ProductId(1, 1L)).thenReturn(false);

        favoriteService.addFavorite(1, 1L);

        verify(favoriteRepository, times(1)).save(any(Favorite.class));
    }

    @Test
    void testAddFavorite_Duplicate_ThrowsException() {
        when(userClient.getUser(1)).thenReturn(ResponseEntity.ok(null));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(favoriteRepository.existsByUserIdAndProduct_ProductId(1, 1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> favoriteService.addFavorite(1, 1L));
    }

    @Test
    void testRemoveFavorite_Success() {
        when(userClient.getUser(1)).thenReturn(ResponseEntity.ok(null));
        when(favoriteRepository.existsByUserIdAndProduct_ProductId(1, 1L)).thenReturn(true);

        favoriteService.removeFavorite(1, 1L);

        verify(favoriteRepository, times(1)).deleteByUserIdAndProduct_ProductId(1, 1L);
    }

    @Test
    void testRemoveFavorite_NotFound_ThrowsException() {
        when(userClient.getUser(1)).thenReturn(ResponseEntity.ok(null));
        when(favoriteRepository.existsByUserIdAndProduct_ProductId(1, 1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> favoriteService.removeFavorite(1, 1L));
    }
}
