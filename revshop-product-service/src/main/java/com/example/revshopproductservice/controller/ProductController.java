package com.example.revshopproductservice.controller;

import com.example.revshopproductservice.model.Product;
import com.example.revshopproductservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Add Product
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    // View All Products (Catalog)
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // Get Product By ID
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // Get Products By Category
    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategory(@PathVariable Integer categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    // Search Products
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }

    // Seller Products
    @GetMapping("/seller/{sellerId}")
    public List<Product> getSellerProducts(@PathVariable Integer sellerId) {
        return productService.getSellerProducts(sellerId);
    }

    // Update Product
    @PutMapping
    public Product updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    // Delete Product
    @DeleteMapping("/{productId}")
    public String deleteProduct(@PathVariable Long productId,
                                @RequestParam Integer sellerId) {

        productService.deleteProduct(productId, sellerId);

        return "Product deleted successfully";
    }
   //Update Stock Quantity
    @PatchMapping("/{productId}/stock")
    public Product updateStock(@PathVariable Long productId,
                               @RequestParam Integer stockQuantity) {

        return productService.updateStock(productId, stockQuantity);
    }
   //Upon Placing the order
    @PatchMapping("/{productId}/reduce-stock")
    public Product reduceStock(@PathVariable Long productId,
                               @RequestParam Integer quantity) {

        return productService.reduceStock(productId, quantity);
    }
    //Upon cancelling or returning the order
    @PatchMapping("/{productId}/add-stock")
    public Product addStock(@PathVariable Long productId,
                            @RequestParam Integer quantity) {

        return productService.addStock(productId, quantity);
    }

    //LowStock Products
    @GetMapping("/low-stock")
    public List<Product> getLowStockProducts() {
        return productService.getLowStockProducts();
    }

}