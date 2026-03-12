package com.example.revshopproductservice.repos;



import com.example.revshopproductservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Integer categoryId);

    List<Product> findBySellerId(Integer sellerId);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByIsActiveTrue();

    List<Product> findByCategoryIdAndIsActiveTrue(Integer categoryId);

    List<Product> findBySellerIdAndIsActiveTrue(Integer sellerId);

    List<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String keyword);

    boolean existsByCategoryId(Integer categoryId);

    List<Product> findByStockQuantityLessThanEqual(Integer threshold);

    @Query("""
SELECT p
FROM Product p
WHERE p.stockQuantity <= p.stockThreshold
AND p.isActive = true
""")
    List<Product> findLowStockProducts();
}
