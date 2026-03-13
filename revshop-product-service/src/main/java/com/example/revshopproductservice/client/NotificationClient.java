package com.example.revshopproductservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revshopproductservice.config.FeignConfig;

@FeignClient(name = "revshop-notification", configuration = FeignConfig.class)
public interface NotificationClient {

    @PostMapping("/notifications/send")
    void sendNotification(@RequestHeader("Authorization") String authHeader,
                          @RequestParam("targetUserId") Integer targetUserId,
                          @RequestParam("message") String message,
                          @RequestParam("type") String type);
}
