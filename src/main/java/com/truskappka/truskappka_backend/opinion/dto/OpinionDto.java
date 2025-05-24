package com.truskappka.truskappka_backend.opinion.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record OpinionDto(
        UUID uuid,
        Rating rating,
        String comment,
        List<String> tags,
        List<String> images
) {}
