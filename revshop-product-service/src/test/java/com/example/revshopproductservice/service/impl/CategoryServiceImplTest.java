package com.example.revshopproductservice.service.impl;

import com.example.revshopproductservice.exception.BadRequestException;
import com.example.revshopproductservice.exception.ResourceNotFoundException;
import com.example.revshopproductservice.model.Category;
import com.example.revshopproductservice.repos.CategoryRepository;
import com.example.revshopproductservice.repos.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setCategoryId(1L);
        testCategory.setCategoryName("Electronics");
    }

    @Test
    void testGetAllCategories() {
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(testCategory));
        List<Category> result = categoryService.getAllCategories();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getCategoryName());
    }

    @Test
    void testGetCategoryById_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        Category result = categoryService.getCategoryById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getCategoryId());
    }

    @Test
    void testGetCategoryById_NullId_ThrowsException() {
        assertThrows(BadRequestException.class, () -> categoryService.getCategoryById(null));
    }

    @Test
    void testGetCategoryById_NotFound_ThrowsException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(99L));
    }

    @Test
    void testAddCategory_Success() {
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
        Category result = categoryService.addCategory(testCategory);
        assertNotNull(result);
        assertEquals("Electronics", result.getCategoryName());
        verify(categoryRepository, times(1)).save(testCategory);
    }

    @Test
    void testAddCategory_NullCategory_ThrowsException() {
        assertThrows(BadRequestException.class, () -> categoryService.addCategory(null));
    }

    @Test
    void testAddCategory_NullName_ThrowsException() {
        Category emptyCategory = new Category();
        assertThrows(BadRequestException.class, () -> categoryService.addCategory(emptyCategory));
    }

    @Test
    void testDeleteCategory_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.existsByCategoryId(1)).thenReturn(false);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).delete(testCategory);
    }

    @Test
    void testDeleteCategory_NotFound_ThrowsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1L));
    }

    @Test
    void testDeleteCategory_ProductsExist_ThrowsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.existsByCategoryId(1)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, never()).delete(any());
    }
}
