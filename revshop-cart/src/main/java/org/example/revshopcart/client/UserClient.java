package org.example.revshopcart.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "revshop-user-service")
public interface UserClient {

    @GetMapping("/user/{userId}")
    Object getUserById(@PathVariable("userId") Integer userId);
}
