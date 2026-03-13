package org.springboot.revshoporderservice.client;

import org.springboot.revshoporderservice.dto.PaymentRequest;
import org.springboot.revshoporderservice.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/payments")
    PaymentResponse makePayment(@RequestHeader("Authorization") String authHeader,
                                @RequestBody PaymentRequest request);
}
