package com.truskappka.truskappka_backend.opinion.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OpinionAddForm(
        UUID standUuid,
        Rating rating,
        String comment,
        List<String> tagNames
) {}