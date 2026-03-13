package org.springboot.revshoporderservice.client;


import org.springboot.revshoporderservice.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/products/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id, @RequestHeader(value = "Authorization", required = false) String authHeader);

    @GetMapping("/products/seller/{sellerId}")
    List<Long> getProductIdsBySeller(@PathVariable("sellerId") Long sellerId, @RequestHeader("Authorization") String authHeader);

    @PutMapping("/products/{productId}/reduce-stock")
    void reduceStock(@PathVariable("productId") Long productId, @RequestParam("quantity") Integer quantity, @RequestHeader("Authorization") String authHeader);
}
