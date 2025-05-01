package com.truskappka.truskappka_backend.image.exception;

public class MinioCustomException extends RuntimeException {

    public MinioCustomException(String message) {
        super(message);
    }
}
