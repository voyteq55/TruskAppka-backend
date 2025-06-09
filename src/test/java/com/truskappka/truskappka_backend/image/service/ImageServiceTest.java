package com.truskappka.truskappka_backend.image.service;

import com.truskappka.truskappka_backend.image.client.MinioClientWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class ImageServiceTest {

    private final String FILENAME = "test.png";
    private final String EXTENSION = "png";
    private final String URL = "http://example.com/test.jpg";

    @Mock
    private MinioClientWrapper minioWrapper;

    @InjectMocks
    private ImageService imageService;

    @Nested
    @DisplayName("uploading image")
    class UploadImageTests {

        @Test
        @DisplayName("should upload image and return its filename")
        void shouldUploadImageAndReturnFilename() throws Exception {
            MultipartFile mockFile = new MockMultipartFile("mock", FILENAME, "image/png", new byte[]{});

            when(minioWrapper.doesObjectExist(anyString())).thenReturn(false);

            String resultFilename = imageService.uploadImage(mockFile);

            assertTrue(resultFilename.endsWith("." + EXTENSION));
            verify(minioWrapper).uploadFile(mockFile, resultFilename);
        }

        @Test
        @DisplayName("should throw an exception when file upload fails")
        void shouldThrowWhenUploadingFails() throws Exception {
            MultipartFile mockFile = new MockMultipartFile("mock", FILENAME, "image/png", new byte[]{});

            when(minioWrapper.doesObjectExist(anyString())).thenReturn(false);
            doThrow(new RuntimeException()).when(minioWrapper).uploadFile(eq(mockFile), anyString());

            RuntimeException exception = assertThrows(RuntimeException.class, () -> imageService.uploadImage(mockFile));

            assertTrue(exception.getMessage().contains("Uploading Image error"));
        }

    }

    @Nested
    @DisplayName("fetching image url by filename")
    class GetImageUrlTests {

        @Test
        @DisplayName("should return url of an image")
        void shouldReturnImageUrl() {
            when(minioWrapper.getImageUrl(FILENAME)).thenReturn(URL);

            String resultUrl = imageService.getImageUrl(FILENAME);

            assertEquals(URL, resultUrl);
            verify(minioWrapper).getImageUrl(FILENAME);
        }

    }
}
