package org.example.revshopeurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class RevshopEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RevshopEurekaServerApplication.class, args);
    }

}
