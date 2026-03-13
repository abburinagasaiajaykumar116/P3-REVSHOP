package com.example.revshopproductservice.service.impl;

import com.example.revshopproductservice.client.NotificationClient;
import com.example.revshopproductservice.client.UserClient;
import com.example.revshopproductservice.exception.BadRequestException;
import com.example.revshopproductservice.exception.ResourceNotFoundException;
import com.example.revshopproductservice.exception.UnauthorizedException;
import com.example.revshopproductservice.model.Product;
import com.example.revshopproductservice.repos.ProductRepository;
import com.example.revshopproductservice.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserClient userClient;
    private final NotificationClient notificationClient;

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackAddProduct")
    @Retry(name = "productService")
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

    public Product fallbackAddProduct(Product product, Throwable t) {
        log.error("Fallback triggered for addProduct due to: {}", t.getMessage());
        throw new BadRequestException("Product addition failed because verification service is down. Please try again.");
    }

    @Override
    public Page<Product> getAllProducts(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productsPage = productRepository.findByIsActiveTrue(pageable);

        for (Product product : productsPage.getContent()) {
            if (product.getImageUrl() == null || "null".equals(product.getImageUrl()))
                product.setImageUrl(null);
        }

        return productsPage;
    }

    @Override
    public Product getProductById(Long productId) {

        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id " + productId));
    }

    @Override
    public Page<Product> getProductsByCategory(Integer categoryId, int page, int size) {

        if (categoryId == null)
            throw new BadRequestException("Category id is required");

        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
    }

    @Override
    public Page<Product> searchProducts(String keyword, int page, int size) {

        if (keyword == null || keyword.trim().isEmpty())
            throw new BadRequestException("Search keyword cannot be empty");

        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByNameOrCategoryNameContainingIgnoreCaseAndIsActiveTrue(keyword, pageable);
    }

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackGetSellerProducts")
    @Retry(name = "productService")
    public Page<Product> getSellerProducts(Integer sellerId, int page, int size) {

        if (sellerId == null)
            throw new BadRequestException("Seller id is required");

        // Validate seller
        try {
            userClient.getUser(sellerId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Seller not found in user service");
        }

        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findBySellerIdAndIsActiveTrue(sellerId, pageable);
    }

    public Page<Product> fallbackGetSellerProducts(Integer sellerId, int page, int size, Throwable t) {
        log.error("Fallback triggered for getSellerProducts due to: {}", t.getMessage());
        return org.springframework.data.domain.Page.empty();
    }

    @Override
    public List<Long> getProductIdsBySeller(Integer sellerId) {
        if (sellerId == null)
            throw new BadRequestException("Seller id is required");

        return productRepository.findProductIdsBySellerIdAndIsActiveTrue(sellerId);
    }

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackUpdateProduct")
    @Retry(name = "productService")
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

    public Product fallbackUpdateProduct(Product product, Throwable t) {
        log.error("Fallback triggered for updateProduct due to: {}", t.getMessage());
        throw new BadRequestException("Update failed because verification service is down. Please try again.");
    }

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackDeleteProduct")
    @Retry(name = "productService")
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

    public void fallbackDeleteProduct(Long productId, Integer sellerId, Throwable t) {
        log.error("Fallback triggered for deleteProduct due to: {}", t.getMessage());
        throw new BadRequestException("Delete failed because verification service is down.");
    }

    @Override
    public Product updateStock(Long productId, Integer stockQuantity, Integer sellerId) {

        if (productId == null || sellerId == null)
            throw new BadRequestException("Product id and seller id are required");

        if (stockQuantity == null || stockQuantity < 0)
            throw new BadRequestException("Stock cannot be negative");

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id " + productId));

        if (!product.getSellerId().equals(sellerId))
            throw new UnauthorizedException("You are not allowed to update this product");

        product.setStockQuantity(stockQuantity);

        if (stockQuantity == 0)
            product.setIsActive(false);
        else
            product.setIsActive(true);

        return productRepository.save(product);
    }

    @Override
    public Product reduceStock(Long productId, Integer quantity, String authHeader) {

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

        // SEND STOCK ALERT NOTIFICATION
        if (product.getStockThreshold() != null && newStock <= product.getStockThreshold()) {
            try {
                notificationClient.sendNotification(authHeader, product.getSellerId(), 
                    "Stock alert! '" + product.getName() + "' (ID: " + productId + ") has reached its threshold. Remaining stock: " + newStock, 
                    "STOCK_ALERT");
            } catch (Exception e) {
                System.out.println("Failed to notify seller for low stock: " + e.getMessage());
            }
        }

        return productRepository.save(product);
    }

    @Override
    public Product addStock(Long productId, Integer quantity, Integer sellerId) {

        if (productId == null)
            throw new BadRequestException("Product id is required");

        if (quantity == null || quantity <= 0)
            throw new BadRequestException("Invalid quantity");

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        if (!product.getSellerId().equals(sellerId))
            throw new UnauthorizedException("You are not allowed to update this product");

        int updatedStock = product.getStockQuantity() + quantity;

        product.setStockQuantity(updatedStock);

        if (updatedStock > 0)
            product.setIsActive(true);

        return productRepository.save(product);
    }

    @Override
    public List<Product> getLowStockProducts(Integer sellerId) {

        if (sellerId == null)
            throw new BadRequestException("Seller id is required");

        List<Product> products = productRepository.findLowStockProductsBySellerId(sellerId);

        if (products.isEmpty())
            throw new ResourceNotFoundException("No low stock products found");

        return products;
    }
}