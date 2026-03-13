package com.example.revshopproductservice.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.example.revshopproductservice.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private ImageServiceImpl imageService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testUploadImage_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "http://res.cloudinary.com/test_image.jpg");

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(uploadResult);

        String result = imageService.uploadImage(file);

        assertNotNull(result);
        assertEquals("http://res.cloudinary.com/test_image.jpg", result);
    }

    @Test
    void testUploadImage_NullFile_ThrowsException() {
        assertThrows(BadRequestException.class, () -> imageService.uploadImage(null));
    }

    @Test
    void testUploadImage_EmptyFile_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);
        assertThrows(BadRequestException.class, () -> imageService.uploadImage(file));
    }

    @Test
    void testUploadImage_UploadFails_ThrowsException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenThrow(new RuntimeException("Cloudinary error"));

        assertThrows(BadRequestException.class, () -> imageService.uploadImage(file));
    }
}
