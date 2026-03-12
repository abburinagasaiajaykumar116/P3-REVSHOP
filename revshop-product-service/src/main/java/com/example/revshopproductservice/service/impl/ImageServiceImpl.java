package com.example.revshopproductservice.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.revshopproductservice.exception.BadRequestException;
import com.example.revshopproductservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file) {

        if (file == null || file.isEmpty())
            throw new BadRequestException("No image file provided");

        try {

            Map uploadResult = cloudinary.uploader()
                    .upload(file.getBytes(), ObjectUtils.emptyMap());

            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {

            throw new BadRequestException("Unable to upload image. Please try again.");
        }
    }
}