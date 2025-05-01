package com.truskappka.truskappka_backend.image.service;

import com.truskappka.truskappka_backend.image.client.MinioClientWrapper;
import lombok.AllArgsConstructor;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@AllArgsConstructor
@Service
public class ImageService {

    private final MinioClientWrapper minioWrapper;


    public String uploadImage(MultipartFile file) throws Exception {
        String extension = FileNameUtils.getExtension(file.getOriginalFilename());
        String uniqueName = getUniqueName(extension);

        minioWrapper.uploadFile(file, uniqueName);
        return uniqueName;
    }

    public String getImageUrl(String filename) {
        return minioWrapper.getImageUrl(filename);
    }

    private String getUniqueName(String extension) {
        String uniqueName;
        do {
            uniqueName = UUID.randomUUID() + "." + extension;
        } while (minioWrapper.doesObjectExist(uniqueName));

        return uniqueName;
    }

}