package com.truskappka.truskappka_backend.stand.dto;

import com.truskappka.truskappka_backend.common.dto.Coordinate;
import com.truskappka.truskappka_backend.common.dto.WorkingHours;

public record StandAddForm(
        String name,
        Coordinate coordinate,
        WorkingHours workingHours
) {}

