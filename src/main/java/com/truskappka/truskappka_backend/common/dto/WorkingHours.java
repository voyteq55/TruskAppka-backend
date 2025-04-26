package com.truskappka.truskappka_backend.common.dto;

public record WorkingHours(
        DayHours monday,
        DayHours tuesday,
        DayHours wednesday,
        DayHours thursday,
        DayHours friday,
        DayHours saturday,
        DayHours sunday
) {}
