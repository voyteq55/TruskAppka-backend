package com.truskappka.truskappka_backend.opinion.dto;

import java.math.BigDecimal;

public record AverageRatingDto(
        BigDecimal averageQualityRating,
        BigDecimal averageServiceRating,
        BigDecimal averagePriceRating
) {}
