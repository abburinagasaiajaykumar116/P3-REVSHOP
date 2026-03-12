package com.example.revshopproductservice.service.impl;

import com.example.revshopproductservice.client.UserClient;
import com.example.revshopproductservice.exception.BadRequestException;
import com.example.revshopproductservice.exception.ResourceNotFoundException;
import com.example.revshopproductservice.exception.UnauthorizedException;
import com.example.revshopproductservice.model.Product;
import com.example.revshopproductservice.repos.ProductRepository;
import com.example.revshopproductservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserClient userClient;

    @Override
    public Product addProduct(Product product) {

        if (product == null)
            throw new BadRequestException("Product data is missing");



        // Validate seller from User Service
        try {
            userClient.getUser(product.getSellerId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Seller not found in user service");
        }

        if (product.getPrice() == null || product.getPrice() <= 0)
            throw new BadRequestException("Product price must be greater than 0");

        if (product.getIsActive() == null)
            product.setIsActive(true);

        if (product.getStockQuantity() != null && product.getStockQuantity() == 0)
            product.setIsActive(false);

        if (product.getMrp() != null && product.getMrp() < 0)
            throw new BadRequestException("MRP cannot be negative");

        if (product.getDiscount() != null && (product.getDiscount() < 0 || product.getDiscount() > 100))
            throw new BadRequestException("Discount must be between 0 and 100");

        if (product.getStockQuantity() != null && product.getStockQuantity() < 0)
            throw new BadRequestException("Stock quantity cannot be negative");

        if (product.getStockThreshold() != null && product.getStockThreshold() < 0)
            throw new BadRequestException("Stock threshold cannot be negative");

        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {

        List<Product> products = productRepository.findByIsActiveTrue();

        for (Product product : products) {
            if (product.getImageUrl() == null || "null".equals(product.getImageUrl()))
                product.setImageUrl(null);
        }

        return products;
    }

    @Override
    public Product getProductById(Long productId) {

        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id " + productId));
    }

    @Override
    public List<Product> getProductsByCategory(Integer categoryId) {

        if (categoryId == null)
            throw new BadRequestException("Category id is required");

        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId);
    }

    @Override
    public List<Product> searchProducts(String keyword) {

        if (keyword == null || keyword.trim().isEmpty())
            throw new BadRequestException("Search keyword cannot be empty");

        return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(keyword);
    }

    @Override
    public List<Product> getSellerProducts(Integer sellerId) {

        if (sellerId == null)
            throw new BadRequestException("Seller id is required");

        // Validate seller
        try {
            userClient.getUser(sellerId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Seller not found in user service");
        }

        return productRepository.findBySellerIdAndIsActiveTrue(sellerId);
    }

    @Override
    public Product updateProduct(Product product) {

        if (product == null || product.getProductId() == null)
            throw new BadRequestException("Product id is required for update");

        if (product.getSellerId() == null)
            throw new BadRequestException("Seller id is required");

        // Validate seller
        try {
            userClient.getUser(product.getSellerId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Seller not found in user service");
        }

        Product existingProduct = productRepository.findById(product.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id " + product.getProductId()));

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setMrp(product.getMrp());
        existingProduct.setDiscount(product.getDiscount());
        existingProduct.setStockQuantity(product.getStockQuantity());
        existingProduct.setStockThreshold(product.getStockThreshold());
        existingProduct.setImageUrl(product.getImageUrl());
        existingProduct.setCategoryId(product.getCategoryId());

        if (existingProduct.getStockQuantity() != null && existingProduct.getStockQuantity() == 0)
            existingProduct.setIsActive(false);

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long productId, Integer sellerId) {

        if (productId == null || sellerId == null)
            throw new BadRequestException("Product id and seller id are required");

        // Validate seller
        try {
            userClient.getUser(sellerId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Seller not found in user service");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id " + productId));

        if (!product.getSellerId().equals(sellerId))
            throw new UnauthorizedException("You are not allowed to delete this product");

        product.setIsActive(false);

        productRepository.save(product);
    }

    @Override
    public Product updateStock(Long productId, Integer stockQuantity) {

        if (productId == null)
            throw new BadRequestException("Product id is required");

        if (stockQuantity == null || stockQuantity < 0)
            throw new BadRequestException("Stock cannot be negative");

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id " + productId));

        product.setStockQuantity(stockQuantity);

        if (stockQuantity == 0)
            product.setIsActive(false);
        else
            product.setIsActive(true);

        return productRepository.save(product);
    }

    @Override
    public Product reduceStock(Long productId, Integer quantity) {

        if (quantity == null || quantity <= 0)
            throw new BadRequestException("Invalid quantity");

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        if (product.getStockQuantity() < quantity)
            throw new BadRequestException("Insufficient stock");

        int newStock = product.getStockQuantity() - quantity;

        product.setStockQuantity(newStock);

        if (newStock == 0)
            product.setIsActive(false);

        return productRepository.save(product);
    }

    @Override
    public Product addStock(Long productId, Integer quantity) {

        if (productId == null)
            throw new BadRequestException("Product id is required");

        if (quantity == null || quantity <= 0)
            throw new BadRequestException("Invalid quantity");

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        int updatedStock = product.getStockQuantity() + quantity;

        product.setStockQuantity(updatedStock);

        if (updatedStock > 0)
            product.setIsActive(true);

        return productRepository.save(product);
    }

    @Override
    public List<Product> getLowStockProducts() {

        List<Product> products = productRepository.findLowStockProducts();

        if (products.isEmpty())
            throw new ResourceNotFoundException("No low stock products found");

        return products;
    }
}