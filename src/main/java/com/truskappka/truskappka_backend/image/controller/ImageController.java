package com.truskappka.truskappka_backend.image.controller;

import com.truskappka.truskappka_backend.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

//This controller will be deleted eventually and is not intended to be used on frontend. only for local testing
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test/images")
public class ImageController {

    private final ImageService imageService;


    @PostMapping
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String filename = imageService.uploadImage(file);
            return ResponseEntity.ok(Collections.singletonMap("filename", filename));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getImage(@RequestParam("filename") String filename) {
        try {
            String url = imageService.getImageUrl(filename);
            return ResponseEntity.ok(Collections.singletonMap("url", url));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}