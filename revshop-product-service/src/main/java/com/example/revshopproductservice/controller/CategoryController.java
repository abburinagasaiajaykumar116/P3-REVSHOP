package com.example.revshopproductservice.controller;

import com.example.revshopproductservice.model.Category;
import com.example.revshopproductservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // Get all categories
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // Get category by id
    @GetMapping("/{categoryId}")
    public Category getCategoryById(@PathVariable Long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    // Add category
    @PostMapping
    public Category addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    // Delete category
    @DeleteMapping("/{categoryId}")
    public String deleteCategory(@PathVariable Long categoryId) {

        categoryService.deleteCategory(categoryId);

        return "Category deleted successfully";
    }
}