package com.example.revshopproductservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "revshop-user-service")
public interface UserClient {

    @GetMapping("/user/{userId}")
    Object getUser(@PathVariable Integer userId);

}