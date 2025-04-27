package com.truskappka.truskappka_backend.opinion.dto;

import java.math.BigDecimal;

public record Rating(
        BigDecimal quality,
        BigDecimal service,
        BigDecimal price
) {}
