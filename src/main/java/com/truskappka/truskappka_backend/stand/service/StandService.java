package com.truskappka.truskappka_backend.stand.service;

import com.truskappka.truskappka_backend.common.exception.ForbiddenAccessException;
import com.truskappka.truskappka_backend.common.exception.ObjectAlreadyExistsException;
import com.truskappka.truskappka_backend.common.exception.ObjectNotFoundException;
import com.truskappka.truskappka_backend.stand.dto.StandAddForm;
import com.truskappka.truskappka_backend.stand.dto.StandDto;
import com.truskappka.truskappka_backend.stand.dto.StandEditForm;
import com.truskappka.truskappka_backend.stand.dto.StandMapMarkerDto;
import com.truskappka.truskappka_backend.stand.entity.Stand;
import com.truskappka.truskappka_backend.stand.repository.StandRepository;
import com.truskappka.truskappka_backend.stand.utils.StandMapper;
import com.truskappka.truskappka_backend.user.entity.User;
import com.truskappka.truskappka_backend.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StandService {

    private final StandRepository standRepository;
    private final UserService userService;

    public List<StandMapMarkerDto> getAllStands() {
        return standRepository.findAll().stream()
                .map(StandMapper::toStandMapMarkerDto)
                .toList();
    }

    public StandDto getStand(UUID standUuid) {
        Stand stand = getStandByUuid(standUuid);
        return StandMapper.toStandDto(stand);
    }

    @Transactional
    public StandDto createStand(StandAddForm standAddForm) {
        User user = userService.getCurrentUser();
        userService.validateUserIsVendor(user);

        validateStandNameIsUnique(standAddForm.name());

        Stand stand = StandMapper.toStand(standAddForm);
        stand.setUser(user);

        Stand savedStand = standRepository.save(stand);
        return StandMapper.toStandDto(savedStand);
    }

    private void validateStandNameIsUnique(String name) {
        if (standRepository.existsByName(name)) {
            throw new ObjectAlreadyExistsException("Stand with name " + name + " already exists");
        }
    }

    @Transactional
    public StandDto updateStand(UUID standUuid, StandEditForm standEditForm) {
        User user = userService.getCurrentUser();
        userService.validateUserIsVendor(user);

        Stand stand = getStandByUuid(standUuid);

        validateStandBelongsToUser(stand, user);

        Optional.ofNullable(standEditForm.name()).ifPresent(name -> {
            validateStandNameIsUnique(name);
            stand.setName(name);
        });

        Optional.ofNullable(standEditForm.coordinate()).ifPresent(coordinate -> {
            stand.setLongitude(coordinate.longitude());
            stand.setLatitude(coordinate.latitude());
        });

        Optional.ofNullable(standEditForm.workingHours()).ifPresent(workingHours -> {
            Optional.ofNullable(workingHours.monday()).ifPresent(monday -> {
                stand.setMondayHoursOpen(monday.open());
                stand.setMondayHoursClose(monday.close());
            });
            Optional.ofNullable(workingHours.tuesday()).ifPresent(tuesday -> {
                stand.setTuesdayHoursOpen(tuesday.open());
                stand.setTuesdayHoursClose(tuesday.close());
            });
            Optional.ofNullable(workingHours.wednesday()).ifPresent(wednesday -> {
                stand.setWednesdayHoursOpen(wednesday.open());
                stand.setWednesdayHoursClose(wednesday.close());
            });
            Optional.ofNullable(workingHours.thursday()).ifPresent(thursday -> {
                stand.setThursdayHoursOpen(thursday.open());
                stand.setThursdayHoursClose(thursday.close());
            });
            Optional.ofNullable(workingHours.friday()).ifPresent(friday -> {
                stand.setFridayHoursOpen(friday.open());
                stand.setFridayHoursClose(friday.close());
            });
            Optional.ofNullable(workingHours.saturday()).ifPresent(saturday -> {
                stand.setSaturdayHoursOpen(saturday.open());
                stand.setSaturdayHoursClose(saturday.close());
            });
            Optional.ofNullable(workingHours.sunday()).ifPresent(sunday -> {
                stand.setSundayHoursOpen(sunday.open());
                stand.setSundayHoursClose(sunday.close());
            });
        });

        return StandMapper.toStandDto(stand);
    }

    private void validateStandBelongsToUser(Stand stand, User user) {
        if (!stand.getUser().getUuid().equals(user.getUuid())) {
            throw new ForbiddenAccessException("You don't have access to modify this stand");
        }
    }

    public Stand getStandByUuid(UUID uuid) {
        return standRepository.findByUuid(uuid).orElseThrow(
                () -> new ObjectNotFoundException("Stand with uuid " + uuid + " not found")
        );
    }

    @Transactional
    public void deleteStand(UUID standUuid) {
        User user = userService.getCurrentUser();
        userService.validateUserIsVendor(user);

        standRepository.deleteByUuid(standUuid);
    }
}
