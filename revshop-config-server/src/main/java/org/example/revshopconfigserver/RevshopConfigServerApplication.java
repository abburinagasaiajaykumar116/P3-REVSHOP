package org.example.revshopconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class RevshopConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RevshopConfigServerApplication.class, args);
    }

}
