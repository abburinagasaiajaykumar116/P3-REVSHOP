package com.example.revshopproductservice.controller;

import com.example.revshopproductservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {

        return imageService.uploadImage(file);
    }
}