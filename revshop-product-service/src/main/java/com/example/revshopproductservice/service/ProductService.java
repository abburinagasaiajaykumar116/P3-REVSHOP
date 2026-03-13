package com.example.revshopproductservice.service;

import com.example.revshopproductservice.model.Product;

import org.springframework.data.domain.Page;
import java.util.List;

public interface ProductService {

    Product addProduct(Product product);

    Page<Product> getAllProducts(int page, int size);

    Product getProductById(Long productId);

    Page<Product> getProductsByCategory(Integer categoryId, int page, int size);

    Page<Product> searchProducts(String keyword, int page, int size);

    Page<Product> getSellerProducts(Integer sellerId, int page, int size);

    List<Long> getProductIdsBySeller(Integer sellerId);

    Product updateProduct(Product product);

    void deleteProduct(Long productId, Integer sellerId);

    Product updateStock(Long productId, Integer stockQuantity, Integer sellerId);
    Product reduceStock(Long productId, Integer quantity, String authHeader);
    Product addStock(Long productId, Integer quantity, Integer sellerId);

    List<Product> getLowStockProducts(Integer sellerId);

}