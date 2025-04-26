package com.truskappka.truskappka_backend.common.dto;

import java.math.BigDecimal;

public record Coordinate(
        BigDecimal longitude,
        BigDecimal latitude
) {}
