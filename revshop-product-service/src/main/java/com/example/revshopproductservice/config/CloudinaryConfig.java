package com.example.revshopproductservice.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(Map.of(
                "cloud_name", "diunbb0ky",
                "api_key", "878823891715674",
                "api_secret", "mzuYUIS6FgWoCwelfCxyTa4xJZw"
        ));
    }
}