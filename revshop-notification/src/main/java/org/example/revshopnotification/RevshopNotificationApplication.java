package org.example.revshopnotification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RevshopNotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(RevshopNotificationApplication.class, args);
    }

}
