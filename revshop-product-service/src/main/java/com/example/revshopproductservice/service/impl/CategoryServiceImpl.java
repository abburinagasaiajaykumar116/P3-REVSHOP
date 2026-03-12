package com.example.revshopproductservice.service.impl;

import com.example.revshopproductservice.exception.BadRequestException;
import com.example.revshopproductservice.exception.ResourceNotFoundException;
import com.example.revshopproductservice.model.Category;
import com.example.revshopproductservice.repos.CategoryRepository;
import com.example.revshopproductservice.repos.ProductRepository;
import com.example.revshopproductservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long categoryId) {

        if (categoryId == null)
            throw new BadRequestException("Category id is required");

        return categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found with id " + categoryId));
    }

    @Override
    public Category addCategory(Category category) {

        if (category == null || category.getCategoryName() == null)
            throw new BadRequestException("Category name is required");

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found with id " + categoryId));

        if (productRepository.existsByCategoryId(categoryId.intValue())) {
            throw new BadRequestException(
                    "Category cannot be deleted because products exist in this category");
        }

        categoryRepository.delete(category);
    }
}