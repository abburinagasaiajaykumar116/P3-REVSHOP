package com.example.revshopproductservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revshopproductservice.config.FeignConfig;

@FeignClient(name = "revshop-user-service", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/user/{userId}")
    Object getUser(@PathVariable Integer userId);

}