package com.truskappka.truskappka_backend.opinion.dto;

import com.truskappka.truskappka_backend.tag.dto.TagDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record OpinionDto(
        UUID uuid,
        Rating rating,
        String comment,
        List<TagDto> tags
) {}
