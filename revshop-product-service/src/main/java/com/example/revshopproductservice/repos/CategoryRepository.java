package com.example.revshopproductservice.repos;

import com.example.revshopproductservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}