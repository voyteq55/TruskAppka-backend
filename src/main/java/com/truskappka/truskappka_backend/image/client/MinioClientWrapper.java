package com.truskappka.truskappka_backend.image.client;

import com.truskappka.truskappka_backend.config.minio.MinioProperties;
import com.truskappka.truskappka_backend.image.exception.MinioCustomException;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RequiredArgsConstructor
@Component
public class MinioClientWrapper {

    private final MinioClient minioClient;
    private final MinioProperties properties;


    public void uploadFile(MultipartFile file, String objectName) throws Exception {
        String bucket = properties.getBucket();

        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

    }

    public String getImageUrl(String objectName) {
        final int EXPIRY_TIME = 60 * 60;
        try {
            String fullUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(properties.getBucket())
                            .object(objectName)
                            .expiry(EXPIRY_TIME)
                            .build()
            );

            URI uri = new URI(fullUrl);
            return uri.getRawPath() + "?" + uri.getRawQuery();
        } catch (Exception e) {
            throw new MinioCustomException("Error generating stripped pre-signed URL" + e);
        }
    }


    public boolean doesObjectExist(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (io.minio.errors.ErrorResponseException e) {
            if (e.response().code() == 404) {
                return false;
            }
            throw new MinioCustomException("MinIO error: " + e.getMessage() + e);
        } catch (Exception e) {
            throw new MinioCustomException("Error checking if object exists" + e);
        }
    }
}