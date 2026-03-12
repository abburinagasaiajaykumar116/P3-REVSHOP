package com.example.revshopproductservice.service;

import com.example.revshopproductservice.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();

    Category getCategoryById(Long categoryId);

    Category addCategory(Category category);

    void deleteCategory(Long categoryId);
}