package com.truskappka.truskappka_backend.common.dto;

import java.time.LocalTime;

public record DayHours(
        LocalTime open,
        LocalTime close
) {}
