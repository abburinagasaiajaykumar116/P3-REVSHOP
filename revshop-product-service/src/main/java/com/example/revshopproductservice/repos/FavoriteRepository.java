package com.example.revshopproductservice.repos;




import com.example.revshopproductservice.dtos.FavoriteView;
import com.example.revshopproductservice.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @Query("""
        SELECT new com.example.revshopproductservice.dtos.FavoriteView(
            p.productId,
            p.name,
            p.description,
            p.price,
            p.imageUrl
        )
        FROM Favorite f
        JOIN f.product p
        WHERE f.userId = :userId
    """)
    List<FavoriteView> findFavoritesByUser(@Param("userId") Integer userId);

    void deleteByUserIdAndProduct_ProductId(Integer userId, Long productId);

    boolean existsByUserIdAndProduct_ProductId(Integer userId, Long productId);
}