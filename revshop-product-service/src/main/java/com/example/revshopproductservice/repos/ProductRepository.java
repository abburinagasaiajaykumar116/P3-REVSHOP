package com.example.revshopproductservice.repos;



import com.example.revshopproductservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Integer categoryId);

    List<Product> findBySellerId(Integer sellerId);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    Page<Product> findByIsActiveTrue(Pageable pageable);

    Page<Product> findByCategoryIdAndIsActiveTrue(Integer categoryId, Pageable pageable);

    Page<Product> findBySellerIdAndIsActiveTrue(Integer sellerId, Pageable pageable);
    
    @Query("SELECT p.productId FROM Product p WHERE p.sellerId = :sellerId AND p.isActive = true")
    List<Long> findProductIdsBySellerIdAndIsActiveTrue(Integer sellerId);

    @Query("SELECT p FROM Product p JOIN Category c ON p.categoryId = c.categoryId WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.isActive = true")
    Page<Product> findByNameOrCategoryNameContainingIgnoreCaseAndIsActiveTrue(String keyword, Pageable pageable);

    boolean existsByCategoryId(Integer categoryId);

    List<Product> findByStockQuantityLessThanEqual(Integer threshold);

    @Query("""
SELECT p
FROM Product p
WHERE p.stockQuantity <= p.stockThreshold
AND p.isActive = true 
AND p.sellerId = :sellerId
""")
    List<Product> findLowStockProductsBySellerId(Integer sellerId);
}
