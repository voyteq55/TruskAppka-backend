package com.truskappka.truskappka_backend.opinion.dto;

import java.util.List;

public record AverageRatingDto(
        double averageQualityRating,
        double averageServiceRating,
        double averagePriceRating,
        List<String> topTags
) {}
