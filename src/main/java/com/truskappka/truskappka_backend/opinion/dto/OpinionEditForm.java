package com.truskappka.truskappka_backend.opinion.dto;

import java.util.List;

public record OpinionEditForm(
        Rating rating,
        String comment,
        List<String> tagNames
) {}
