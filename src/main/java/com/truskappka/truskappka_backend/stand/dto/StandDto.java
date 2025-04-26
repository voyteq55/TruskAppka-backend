package com.truskappka.truskappka_backend.stand.dto;

import com.truskappka.truskappka_backend.common.dto.Coordinate;
import com.truskappka.truskappka_backend.common.dto.WorkingHours;
import lombok.Builder;

import java.util.UUID;

@Builder
public record StandDto(
        String name,
        UUID uuid,
        Coordinate coordinate,
        WorkingHours workingHours
) {}

