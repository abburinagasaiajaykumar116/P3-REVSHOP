package org.springboot.revshoporderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "revshop-user-service")
public interface UserClient {
    @GetMapping("/user/{userId}")
    Map<String, Object> getUserById(@PathVariable("userId") Long userId);
}
