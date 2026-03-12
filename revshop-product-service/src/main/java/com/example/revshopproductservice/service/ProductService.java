package com.example.revshopproductservice.service;

import com.example.revshopproductservice.model.Product;

import java.util.List;

public interface ProductService {

    Product addProduct(Product product);

    List<Product> getAllProducts();

    Product getProductById(Long productId);

    List<Product> getProductsByCategory(Integer categoryId);

    List<Product> searchProducts(String keyword);

    List<Product> getSellerProducts(Integer sellerId);

    Product updateProduct(Product product);

    void deleteProduct(Long productId, Integer sellerId);

    Product updateStock(Long productId, Integer stockQuantity);

    Product reduceStock(Long productId, Integer quantity);

    Product addStock(Long productId, Integer quantity);

    List<Product> getLowStockProducts();

}