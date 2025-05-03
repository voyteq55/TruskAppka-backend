package com.truskappka.truskappka_backend.auth.dto;

public record TokenDto(
        String access,
        String refresh
) {
}
