package com.truskappka.truskappka_backend.stand.utils;

import com.truskappka.truskappka_backend.common.dto.Coordinate;
import com.truskappka.truskappka_backend.common.dto.DayHours;
import com.truskappka.truskappka_backend.common.dto.WorkingHours;
import com.truskappka.truskappka_backend.stand.dto.StandAddForm;
import com.truskappka.truskappka_backend.stand.dto.StandDto;
import com.truskappka.truskappka_backend.stand.dto.StandMapMarkerDto;
import com.truskappka.truskappka_backend.stand.entity.Stand;
import lombok.experimental.UtilityClass;

import java.time.LocalTime;
import java.util.UUID;

@UtilityClass
public class StandMapper {

    public Stand toStand(StandAddForm standAddForm) {
        return Stand.builder()
                .name(standAddForm.name())
                .uuid(UUID.randomUUID())
                .longitude(standAddForm.coordinate().longitude())
                .latitude(standAddForm.coordinate().latitude())
                .mondayHoursOpen(standAddForm.workingHours().monday().open())
                .mondayHoursClose(standAddForm.workingHours().monday().close())
                .tuesdayHoursOpen(standAddForm.workingHours().tuesday().open())
                .tuesdayHoursClose(standAddForm.workingHours().tuesday().close())
                .wednesdayHoursOpen(standAddForm.workingHours().wednesday().open())
                .wednesdayHoursClose(standAddForm.workingHours().wednesday().close())
                .thursdayHoursOpen(standAddForm.workingHours().thursday().open())
                .thursdayHoursClose(standAddForm.workingHours().thursday().close())
                .fridayHoursOpen(standAddForm.workingHours().friday().open())
                .fridayHoursClose(standAddForm.workingHours().friday().close())
                .saturdayHoursOpen(standAddForm.workingHours().saturday().open())
                .saturdayHoursClose(standAddForm.workingHours().saturday().close())
                .sundayHoursOpen(standAddForm.workingHours().sunday().open())
                .sundayHoursClose(standAddForm.workingHours().sunday().close())
                .build();
    }

    public StandDto toStandDto(Stand stand) {
        return StandDto.builder()
                .name(stand.getName())
                .uuid(stand.getUuid())
                .coordinate(new Coordinate(stand.getLongitude(), stand.getLatitude()))
                .workingHours(mapWorkingHours(stand))
                .build();
    }

    private WorkingHours mapWorkingHours(Stand stand) {
        return new WorkingHours(
                mapDayHours(stand.getMondayHoursOpen(), stand.getMondayHoursClose()),
                mapDayHours(stand.getTuesdayHoursOpen(), stand.getTuesdayHoursClose()),
                mapDayHours(stand.getWednesdayHoursOpen(), stand.getWednesdayHoursClose()),
                mapDayHours(stand.getThursdayHoursOpen(), stand.getThursdayHoursClose()),
                mapDayHours(stand.getFridayHoursOpen(), stand.getFridayHoursClose()),
                mapDayHours(stand.getSaturdayHoursOpen(), stand.getSaturdayHoursClose()),
                mapDayHours(stand.getSundayHoursOpen(), stand.getSundayHoursClose())
        );
    }

    private DayHours mapDayHours(LocalTime open, LocalTime close) {
        return new DayHours(open, close);
    }

    public StandMapMarkerDto toStandMapMarkerDto(Stand stand) {
        return StandMapMarkerDto.builder()
                .uuid(stand.getUuid())
                .coordinate(new Coordinate(stand.getLongitude(), stand.getLatitude()))
                .build();
    }
}
