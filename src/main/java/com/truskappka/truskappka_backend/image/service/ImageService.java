package com.truskappka.truskappka_backend.image.service;

import com.truskappka.truskappka_backend.image.client.MinioClientWrapper;
import com.truskappka.truskappka_backend.image.exception.MinioCustomException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final MinioClientWrapper minioWrapper;


    public String uploadImage(MultipartFile file) throws MinioCustomException {
        String extension = FileNameUtils.getExtension(file.getOriginalFilename());
        String uniqueName = getUniqueName(extension);

        try {
            minioWrapper.uploadFile(file, uniqueName);
        } catch (Exception e) {
            throw new RuntimeException("Uploading Image error" + e);
        }
        return uniqueName;
    }

    public String getImageUrl(String filename) {
        return minioWrapper.getImageUrl(filename);
    }

    private String getUniqueName(String extension) {
        String uniqueName;
        do {
            // TODO handle gracefully
            uniqueName = UUID.randomUUID() + "." + extension;
        } while (minioWrapper.doesObjectExist(uniqueName));

        return uniqueName;
    }

}