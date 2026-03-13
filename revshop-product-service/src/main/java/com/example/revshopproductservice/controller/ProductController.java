package com.example.revshopproductservice.controller;

import com.example.revshopproductservice.model.Product;
import com.example.revshopproductservice.service.ImageService;
import com.example.revshopproductservice.service.ProductService;
import com.example.revshopproductservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ImageService imageService;
    private final JwtUtil jwtUtil;

    private Integer getUserIdOrThrow(String authHeader) {
        Integer userId = jwtUtil.extractUserIdFromHeader(authHeader);
        if (userId == null) {
            throw new RuntimeException("Unauthorized: Unable to extract User ID from token");
        }
        return userId;
    }

    // Add Product
    @PostMapping
    public ResponseEntity<Map<String, String>> addProduct(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam Double mrp,
            @RequestParam Double discount,
            @RequestParam Integer stockQuantity,
            @RequestParam Integer stockThreshold,
            @RequestParam Integer categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        Integer sellerId;
        try {
            sellerId = getUserIdOrThrow(authHeader);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized: Invalid or missing token"));
        }

        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setMrp(mrp);
        product.setDiscount(discount);
        product.setStockQuantity(stockQuantity);
        product.setStockThreshold(stockThreshold);
        product.setCategoryId(categoryId);

        if (image != null && !image.isEmpty()) {
            String imageUrl = imageService.uploadImage(image);
            product.setImageUrl(imageUrl);
        }

        productService.addProduct(product);

        return ResponseEntity.ok(Map.of("message", "Product added successfully!"));
    }

    @GetMapping
    public Page<Product> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getAllProducts(page, size);
    }

    // Get Product By ID
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/category/{categoryId}")
    public Page<Product> getProductsByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getProductsByCategory(categoryId, page, size);
    }

    @GetMapping("/search")
    public Page<Product> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.searchProducts(keyword, page, size);
    }

    @GetMapping("/seller")
    public Page<Product> getSellerProducts(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Integer sellerId = getUserIdOrThrow(authHeader);
        return productService.getSellerProducts(sellerId, page, size);
    }

    // Seller Product IDs (For Internal Feign Client usage by Order Service)
    @GetMapping("/seller/{sellerId}")
    public List<Long> getProductIdsBySeller(
            @PathVariable Integer sellerId,
            @RequestHeader("Authorization") String authHeader) {
        
        getUserIdOrThrow(authHeader); // validates token
        return productService.getProductIdsBySeller(sellerId);
    }

    // Update Product
    @PutMapping
    public Product updateProduct(@RequestBody Product product,
                                 @RequestHeader("Authorization") String authHeader) {
        Integer sellerId = getUserIdOrThrow(authHeader);
        product.setSellerId(sellerId); // Enforce the seller ID from token
        return productService.updateProduct(product);
    }

    // Delete Product
    @DeleteMapping("/{productId}")
    public String deleteProduct(@PathVariable Long productId,
                                @RequestHeader("Authorization") String authHeader) {

        Integer sellerId = getUserIdOrThrow(authHeader);
        productService.deleteProduct(productId, sellerId);

        return "Product deleted successfully";
    }
   //Update Stock Quantity
    @PutMapping("/{productId}/stock")
    public Product updateStock(@PathVariable Long productId,
                               @RequestParam Integer stockQuantity,
                               @RequestHeader("Authorization") String authHeader) {

        Integer sellerId = getUserIdOrThrow(authHeader);
        return productService.updateStock(productId, stockQuantity, sellerId);
    }

    //Upon Placing the order
    @PutMapping("/{productId}/reduce-stock")
    public Product reduceStock(@PathVariable Long productId,
                               @RequestParam Integer quantity,
                               @RequestHeader("Authorization") String authHeader) {

        // Validate token exists, but do not force token user == seller
        getUserIdOrThrow(authHeader);
        return productService.reduceStock(productId, quantity, authHeader);
    }
    //Upon cancelling or returning the order
    @PutMapping("/{productId}/add-stock")
    public Product addStock(@PathVariable Long productId,
                            @RequestParam Integer quantity,
                            @RequestHeader("Authorization") String authHeader) {

        Integer sellerId = getUserIdOrThrow(authHeader);
        return productService.addStock(productId, quantity, sellerId);
    }

    //LowStock Products
    @GetMapping("/low-stock")
    public List<Product> getLowStockProducts(@RequestHeader("Authorization") String authHeader) {
        Integer sellerId = getUserIdOrThrow(authHeader);
        return productService.getLowStockProducts(sellerId);
    }

}