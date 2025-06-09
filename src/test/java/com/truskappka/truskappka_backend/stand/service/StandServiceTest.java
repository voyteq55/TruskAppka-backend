package com.truskappka.truskappka_backend.stand.service;

import com.truskappka.truskappka_backend.common.dto.Coordinate;
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
import com.truskappka.truskappka_backend.user.exception.UserNotVendorException;
import com.truskappka.truskappka_backend.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class StandServiceTest {

    private final UUID USER_UUID = UUID.randomUUID();
    private final UUID STAND_UUID = UUID.randomUUID();
    private final List<UUID> STAND_UUIDS = List.of(STAND_UUID, UUID.randomUUID(), UUID.randomUUID());
    private final String STAND_NAME = "stand name";

    @Mock
    private StandRepository standRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private StandService standService;

    @Nested
    @DisplayName("retrieving stands located within radius from a specified point")
    class GetStandsWithinRadiusTests {

        @Test
        @DisplayName("should return all stands within specified radius")
        void shouldReturnStandsWithinRadius() {
            List<Stand> standsWithinRadius = STAND_UUIDS.stream()
                    .map(uuid -> Stand.builder().uuid(uuid).build())
                    .toList();
            List<StandMapMarkerDto> expectedDtos = STAND_UUIDS.stream()
                    .map(uuid -> StandMapMarkerDto.builder().uuid(uuid).coordinate(new Coordinate(0, 0)).build())
                    .toList();

            when(standRepository.findAllWithinRadius(1.0, 2.0, 3.0)).thenReturn(standsWithinRadius);

            List<StandMapMarkerDto> resultDtos = standService.getStandsWithinRadius(1.0, 2.0, 3.0);

            assertEquals(expectedDtos, resultDtos);
        }

    }

    @Nested
    @DisplayName("retrieving all stands")
    class GetAllStandsTests {

        @Test
        @DisplayName("should return all stands")
        void shouldReturnAllStandsMappedToDtos() {
            List<Stand> stands = STAND_UUIDS.stream()
                    .map(uuid -> Stand.builder().uuid(uuid).build())
                    .toList();
            List<StandMapMarkerDto> expectedDtos = STAND_UUIDS.stream()
                    .map(uuid -> StandMapMarkerDto.builder().uuid(uuid).coordinate(new Coordinate(0.0, 0.0)).build())
                    .toList();

            when(standRepository.findAll()).thenReturn(stands);

            List<StandMapMarkerDto> resultDtos = standService.getAllStands();

            assertEquals(expectedDtos, resultDtos);
        }

    }

    @Nested
    @DisplayName("retrieving stand by uuid")
    class GetStandTests {

        @Test
        @DisplayName("should return stand with a specified uuid")
        public void shouldReturnStandMappedToDto() {
            try (MockedStatic<StandMapper> standMapperMockedStatic = mockStatic(StandMapper.class)) {
                Stand stand = Stand.builder().uuid(STAND_UUID).build();
                StandDto expectedDto = StandDto.builder().uuid(STAND_UUID).build();

                when(standRepository.findByUuid(STAND_UUID)).thenReturn(Optional.of(stand));
                standMapperMockedStatic.when(() -> StandMapper.toStandDto(stand)).thenReturn(expectedDto);

                StandDto resultDto = standService.getStand(STAND_UUID);

                assertEquals(expectedDto, resultDto);
            }
        }

    }

    @Nested
    @DisplayName("creating a new stand")
    class CreateStandTests {

        @Test
        @DisplayName("should create and return a new stand")
        public void shouldCreateStandAndReturnDto() {
            User user = new User();
            user.setVendor(true);
            StandAddForm standAddForm = new StandAddForm(STAND_NAME, null, null);
            Stand stand = Stand.builder().uuid(STAND_UUID).name(STAND_NAME).build();
            StandDto expectedStandDto = StandDto.builder().uuid(STAND_UUID).name(STAND_NAME).build();

            when(userService.getCurrentUser()).thenReturn(user);
            doNothing().when(userService).validateUserIsVendor(user);
            when(standRepository.existsByName(STAND_NAME)).thenReturn(false);

            try (MockedStatic<StandMapper> standMapperMockedStatic = mockStatic(StandMapper.class)) {
                standMapperMockedStatic.when(() -> StandMapper.toStand(standAddForm)).thenReturn(stand);
                when(standRepository.save(stand)).thenReturn(stand);
                standMapperMockedStatic.when(() -> StandMapper.toStandDto(stand)).thenReturn(expectedStandDto);

                StandDto resultStandDto = standService.createStand(standAddForm);

                assertEquals(expectedStandDto, resultStandDto);
                verify(standRepository).save(stand);
            }
        }

        @Test
        @DisplayName("should throw an exception when the current user is not vendor")
        public void shouldThrowWhenUserIsNotVendor() {
            User user = new User();
            user.setVendor(true);
            StandAddForm standAddForm = new StandAddForm(STAND_NAME, null, null);

            when(userService.getCurrentUser()).thenReturn(user);
            doThrow(new UserNotVendorException("User has no access to create stands")).when(userService).validateUserIsVendor(user);

            assertThrows(UserNotVendorException.class, () -> standService.createStand(standAddForm));
        }

        @Test
        @DisplayName("should throw an exception when the chosen name for a stand already exists")
        public void shouldThrowWhenStandNameAlreadyExists() {
            User user = new User();
            user.setVendor(true);
            StandAddForm standAddForm = new StandAddForm(STAND_NAME, null, null);

            when(userService.getCurrentUser()).thenReturn(user);
            doNothing().when(userService).validateUserIsVendor(user);
            when(standRepository.existsByName(STAND_NAME)).thenReturn(true);

            assertThrows(ObjectAlreadyExistsException.class, () -> standService.createStand(standAddForm));
        }
    }

    @Nested
    @DisplayName("updating an existing stand")
    class UpdateStandTests {

        @Test
        @DisplayName("should update and return an existing stand")
        public void shouldUpdateStandAndReturnDto() {
            User user = new User();
            user.setVendor(true);
            StandEditForm standEditForm = new StandEditForm(null, new Coordinate(1.0, 2.0), null);
            StandDto expectedUpdatedStandDto = StandDto.builder()
                    .uuid(STAND_UUID)
                    .name(STAND_NAME)
                    .coordinate(new Coordinate(1.0, 2.0))
                    .build();
            Stand editedStand = Stand.builder().uuid(STAND_UUID).name(STAND_NAME).user(user).build();

            when(userService.getCurrentUser()).thenReturn(user);
            doNothing().when(userService).validateUserIsVendor(user);
            when(standRepository.findByUuid(STAND_UUID)).thenReturn(Optional.of(editedStand));

            try (MockedStatic<StandMapper> standMapperMockedStatic = mockStatic(StandMapper.class)) {
                standMapperMockedStatic.when(() -> StandMapper.toStandDto(editedStand)).thenReturn(expectedUpdatedStandDto);

                StandDto resultDto = standService.updateStand(STAND_UUID, standEditForm);

                assertEquals(1.0, editedStand.getLongitude());
                assertEquals(2.0, editedStand.getLatitude());
                assertEquals(expectedUpdatedStandDto, resultDto);
            }
        }

        @Test
        @DisplayName("should throw an exception when the current user is not vendor")
        public void shouldThrowWhenUserIsNotVendor() {
            User user = new User();
            user.setVendor(true);
            StandEditForm standEditForm = new StandEditForm(STAND_NAME, null, null);

            when(userService.getCurrentUser()).thenReturn(user);
            doThrow(new UserNotVendorException("User has no access to create stands")).when(userService).validateUserIsVendor(user);

            assertThrows(UserNotVendorException.class, () -> standService.updateStand(STAND_UUID, standEditForm));
        }

        @Test
        @DisplayName("should throw an exception when the new chosen name for a stand already exists")
        public void shouldThrowWhenNewStandNameAlreadyExists() {
            User user = new User();
            user.setVendor(true);
            StandEditForm standEditForm = new StandEditForm(STAND_NAME, null, null);
            Stand editedStand = Stand.builder().uuid(STAND_UUID).name(STAND_NAME).user(user).build();
            Stand existingStand = Stand.builder().uuid(UUID.randomUUID()).name(STAND_NAME).user(user).build();

            when(userService.getCurrentUser()).thenReturn(user);
            doNothing().when(userService).validateUserIsVendor(user);
            when(standRepository.findByUuid(STAND_UUID)).thenReturn(Optional.of(editedStand));
            when(standRepository.findByName(STAND_NAME)).thenReturn(Optional.of(existingStand));

            assertThrows(ObjectAlreadyExistsException.class, () -> standService.updateStand(STAND_UUID, standEditForm));
        }

    }

    @Nested
    @DisplayName("retrieving stand by uuid")
    class GetStandByUuidTests {
        @Test
        @DisplayName("should return stand by uuid")
        public void shouldReturnStandByUuid() {
            Stand stand = new Stand();

            when(standRepository.findByUuid(STAND_UUID)).thenReturn(Optional.of(stand));

            Stand result = standService.getStandByUuid(STAND_UUID);

            assertEquals(stand, result);
        }

        @Test
        @DisplayName("should throw an exception when no stand with specified uuid found")
        public void shouldThrowWhenStandNotFound() {
            when(standRepository.findByUuid(STAND_UUID)).thenReturn(Optional.empty());

            assertThrows(ObjectNotFoundException.class, () -> standService.getStandByUuid(STAND_UUID));
        }
    }

    @Nested
    @DisplayName("deleting a stand")
    class DeleteStandTests {

        @Test
        @DisplayName("should delete an existing stand")
        public void shouldDeleteStand() {
            User user = new User();
            user.setUuid(USER_UUID);
            user.setVendor(true);

            when(userService.getCurrentUser()).thenReturn(user);
            doNothing().when(userService).validateUserIsVendor(user);

            standService.deleteStand(STAND_UUID);

            verify(userService).validateUserIsVendor(user);
            verify(standRepository).deleteByUuid(STAND_UUID);
        }

        @Test
        @DisplayName("should throw an exception when the current user is not vendor")
        public void shouldThrowWhenUserIsNotVendor() {
            User user = new User();
            user.setUuid(USER_UUID);
            user.setVendor(false);

            doThrow(new UserNotVendorException("User has no access to create stands")).when(userService).validateUserIsVendor(user);
            when(userService.getCurrentUser()).thenReturn(user);

            assertThrows(UserNotVendorException.class, () -> standService.deleteStand(STAND_UUID));
            verify(userService).validateUserIsVendor(user);
        }

    }
}
