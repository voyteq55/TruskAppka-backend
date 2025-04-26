package com.truskappka.truskappka_backend.stand.dto;

import com.truskappka.truskappka_backend.common.dto.Coordinate;
import lombok.Builder;

import java.util.UUID;

@Builder
public record StandMapMarkerDto (
        UUID uuid,
        Coordinate coordinate
) {}
