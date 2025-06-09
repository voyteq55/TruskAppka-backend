package com.truskappka.truskappka_backend.user.service;

import com.truskappka.truskappka_backend.common.exception.ObjectNotFoundException;
import com.truskappka.truskappka_backend.config.security.AuthContext;
import com.truskappka.truskappka_backend.image.service.ImageService;
import com.truskappka.truskappka_backend.opinion.dto.OpinionDto;
import com.truskappka.truskappka_backend.opinion.entity.Opinion;
import com.truskappka.truskappka_backend.opinion.repository.OpinionRepository;
import com.truskappka.truskappka_backend.opinion.utils.OpinionMapper;
import com.truskappka.truskappka_backend.user.dto.IsVendorDto;
import com.truskappka.truskappka_backend.user.entity.User;
import com.truskappka.truskappka_backend.user.exception.UserNotVendorException;
import com.truskappka.truskappka_backend.user.repository.UserRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class UserServiceTest {

    private final UUID USER_UUID = UUID.randomUUID();
    private final List<UUID> OPINION_UUIDS = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    private final String EMAIL = "mail@example.com";

    @Mock
    private UserRepository userRepository;
    @Mock
    private OpinionRepository opinionRepository;
    @Mock
    private ImageService imageService;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("retrieving current user")
    class GetCurrentUserTests {

        @Test
        @DisplayName("should return current user")
        public void shouldReturnCurrentUser() {
            User expectedCurrentUser = new User();
            expectedCurrentUser.setUuid(USER_UUID);

            try (MockedStatic<AuthContext> authContextMockedStatic = mockStatic(AuthContext.class)) {
                authContextMockedStatic.when(AuthContext::getUserId).thenReturn(USER_UUID);
                when(userRepository.findByUuid(USER_UUID)).thenReturn(Optional.of(expectedCurrentUser));

                User resultUser = userService.getCurrentUser();

                assertEquals(expectedCurrentUser, resultUser);
            }
        }

        @Test
        @DisplayName("should throw an exception when no current user was set")
        public void shouldThrowWhenUserNotFound() {
            try (MockedStatic<AuthContext> authContextMockedStatic = mockStatic(AuthContext.class)) {
                authContextMockedStatic.when(AuthContext::getUserId).thenReturn(null);
                when(userRepository.findByUuid(null)).thenReturn(Optional.empty());

                assertThrows(ObjectNotFoundException.class, () -> userService.getCurrentUser());
            }
        }
    }

    @Nested
    @DisplayName("setting current user as vendor")
    class SetUserAsVendorTests {

        @Test
        @DisplayName("should set current user as vendor")
        public void shouldSetCurrentUserAsVendor() {
            User currentUser = new User();
            currentUser.setUuid(USER_UUID);
            assertFalse(currentUser.isVendor());

            try (MockedStatic<AuthContext> authContextMockedStatic = mockStatic(AuthContext.class)) {
                authContextMockedStatic.when(AuthContext::getUserId).thenReturn(USER_UUID);
                when(userRepository.findByUuid(USER_UUID)).thenReturn(Optional.of(currentUser));

                userService.setUserAsVendor();

                assertTrue(currentUser.isVendor());
            }
        }

        @Test
        @DisplayName("should throw exception when user not found")
        public void shouldThrowWhenUserNotFound() {
            try (MockedStatic<AuthContext> authContextMockedStatic = mockStatic(AuthContext.class)) {
                authContextMockedStatic.when(AuthContext::getUserId).thenReturn(null);
                when(userRepository.findByUuid(null)).thenReturn(Optional.empty());

                assertThrows(ObjectNotFoundException.class, () -> userService.setUserAsVendor());
            }
        }

    }

    @Nested
    @DisplayName("finding user by email and creating when no user with such email exists")
    class FindOrCreateByEmailTests {

        @Test
        @DisplayName("should return an existing user with specified email")
        void shouldReturnExistingUserUuid() {
            User existingUser = new User(EMAIL);
            existingUser.setUuid(USER_UUID);

            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existingUser));

            UUID resultUuid = userService.findOrCreateByEmail(EMAIL);

            assertEquals(USER_UUID, resultUuid);
        }

        @Test
        @DisplayName("should create and return a new user when email was not linked to any existing user")
        void shouldCreateAndReturnNewUserUuid() {
            User newUser = new User(EMAIL);
            newUser.setUuid(USER_UUID);

            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenReturn(newUser);

            UUID resultUuid = userService.findOrCreateByEmail(EMAIL);

            assertEquals(USER_UUID, resultUuid);
        }

    }

    @Nested
    @DisplayName("validating that the current user is vendor")
    class ValidateUserIsVendor {

        @Test
        @DisplayName("should not throw when current user is vendor")
        void shouldNotThrowIfUserIsVendor() {
            User user = new User();
            user.setVendor(true);

            assertDoesNotThrow(() -> userService.validateUserIsVendor(user));
        }

        @Test
        @DisplayName("should throw an exception when current user is not vendor")
        void shouldThrowIfUserIsNotVendor() {
            User user = new User();
            user.setVendor(false);

            assertThrows(UserNotVendorException.class, () -> userService.validateUserIsVendor(user));
        }

    }

    @Nested
    @DisplayName("checking if the current user is vendor")
    class IsVendorTests {

        @Test
        @DisplayName("should return true when the current user is vendor")
        void shouldReturnTrueWhenUserIsVendor() {
            User user = new User();
            user.setUuid(USER_UUID);
            user.setVendor(true);

            try (MockedStatic<AuthContext> authContextMockedStatic = mockStatic(AuthContext.class)) {
                authContextMockedStatic.when(AuthContext::getUserId).thenReturn(USER_UUID);
                when(userRepository.findByUuid(USER_UUID)).thenReturn(Optional.of(user));

                IsVendorDto result = userService.isVendor();

                assertTrue(result.isVendor());
            }
        }

        @Test
        @DisplayName("should return false when the current user is not vendor")
        void shouldReturnFalseWhenUserIsNotVendor() {
            User user = new User();
            user.setUuid(USER_UUID);
            user.setVendor(false);

            try (MockedStatic<AuthContext> authContextMockedStatic = mockStatic(AuthContext.class)) {
                authContextMockedStatic.when(AuthContext::getUserId).thenReturn(USER_UUID);
                when(userRepository.findByUuid(USER_UUID)).thenReturn(Optional.of(user));

                IsVendorDto result = userService.isVendor();

                assertFalse(result.isVendor());
                verify(userRepository).findByUuid(USER_UUID);
            }
        }

    }

    @Nested
    @DisplayName("retrieving all opinions of the current user")
    class GetUserOpinionsTests {

        @Test
        @DisplayName("should return user's opinions")
        void shouldReturnUserOpinionsMappedToDtos() {
            User user = new User();
            user.setUuid(USER_UUID);

            List<Opinion> userOpinions = OPINION_UUIDS.stream().map(uuid -> Opinion.builder().uuid(uuid).build()).toList();
            List<OpinionDto> expectedDtos = OPINION_UUIDS.stream().map(uuid -> OpinionDto.builder().uuid(uuid).build()).toList();

            try (MockedStatic<AuthContext> authContextMockedStatic = mockStatic(AuthContext.class); MockedStatic<OpinionMapper> opinionMapperMockedStatic = mockStatic(OpinionMapper.class)) {
                authContextMockedStatic.when(AuthContext::getUserId).thenReturn(USER_UUID);
                when(userRepository.findByUuid(USER_UUID)).thenReturn(Optional.of(user));
                when(opinionRepository.findByUser(user)).thenReturn(userOpinions);
                for (int i = 0; i < OPINION_UUIDS.size(); i++) {
                    int finalI = i;
                    opinionMapperMockedStatic.when(() -> OpinionMapper.toOpinionDto(userOpinions.get(finalI), imageService)).thenReturn(expectedDtos.get(finalI));
                }

                List<OpinionDto> resultDtos = userService.getUserOpinions();

                assertEquals(expectedDtos, resultDtos);
            }
        }
    }
}
