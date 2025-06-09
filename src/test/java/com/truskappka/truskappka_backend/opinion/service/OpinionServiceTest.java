package com.truskappka.truskappka_backend.opinion.service;

import com.truskappka.truskappka_backend.common.exception.ForbiddenAccessException;
import com.truskappka.truskappka_backend.common.exception.ObjectNotFoundException;
import com.truskappka.truskappka_backend.image.service.ImageService;
import com.truskappka.truskappka_backend.opinion.dto.AverageRatingDto;
import com.truskappka.truskappka_backend.opinion.dto.OpinionAddForm;
import com.truskappka.truskappka_backend.opinion.dto.OpinionDto;
import com.truskappka.truskappka_backend.opinion.dto.Rating;
import com.truskappka.truskappka_backend.opinion.entity.Opinion;
import com.truskappka.truskappka_backend.opinion.repository.OpinionRepository;
import com.truskappka.truskappka_backend.opinion.utils.OpinionMapper;
import com.truskappka.truskappka_backend.stand.entity.Stand;
import com.truskappka.truskappka_backend.stand.service.StandService;
import com.truskappka.truskappka_backend.tag.entity.Tag;
import com.truskappka.truskappka_backend.tag.repository.TagRepository;
import com.truskappka.truskappka_backend.user.entity.User;
import com.truskappka.truskappka_backend.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class OpinionServiceTest {

    private final UUID STAND_UUID = UUID.randomUUID();
    private final UUID OPINION_UUID = UUID.randomUUID();
    private final List<UUID> OPINION_UUIDS = List.of(OPINION_UUID, UUID.randomUUID(), UUID.randomUUID());

    private User user;

    @Mock
    private OpinionRepository opinionRepository;
    @Mock
    private UserService userService;
    @Mock
    private StandService standService;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private ImageService imageService;

    @InjectMocks
    private OpinionService opinionService;

    @BeforeEach
    public void setup() {
        user = new User();
    }

    @Nested
    @DisplayName("retrieving opinions of a stand")
    class GetOpinionsForStandTests {

        @Test
        @DisplayName("should return opinions of a stand")
        public void shouldReturnOpinionsForStandMappedToDtos() {
            Stand stand = Stand.builder().uuid(STAND_UUID).build();

            List<Opinion> standOpinions = OPINION_UUIDS.stream().map(uuid -> Opinion.builder().uuid(uuid).stand(stand).build()).toList();
            List<OpinionDto> expectedDtos = OPINION_UUIDS.stream().map(uuid -> OpinionDto.builder().uuid(uuid).build()).toList();

            when(standService.getStandByUuid(STAND_UUID)).thenReturn(stand);
            when(opinionRepository.findByStand(stand)).thenReturn(standOpinions);

            try (MockedStatic<OpinionMapper> opinionMapperMockedStatic = mockStatic(OpinionMapper.class)) {
                for (int i = 0; i < OPINION_UUIDS.size(); i++) {
                    int finalI = i;
                    opinionMapperMockedStatic.when(() -> OpinionMapper.toOpinionDto(standOpinions.get(finalI), imageService)).thenReturn(expectedDtos.get(finalI));
                }

                List<OpinionDto> resultDtos = opinionService.getOpinionsForStand(STAND_UUID);

                assertEquals(expectedDtos, resultDtos);
            }
        }

        @Test
        @DisplayName("should throw an exception when no stand with a specified uuid was found")
        public void shouldThrowWhenStandWithUuidNotFound() {
            doThrow(new ObjectNotFoundException("Stand with uuid " + STAND_UUID + " not found")).when(standService).getStandByUuid(STAND_UUID);

            assertThrows(ObjectNotFoundException.class, () -> opinionService.getOpinionsForStand(STAND_UUID));
        }
    }

    @Nested
    @DisplayName("calculating average ratings of a stand")
    class CalculateAverageRatingsTests {

        @Test
        @DisplayName("should return average ratings of zero when no opinions have been written yet for a stand")
        public void shouldReturnAverageRatingsOfZeroWhenNoOpinionsFound() {
            Tag tag1 = Tag.builder().name("tag1").build();
            Tag tag2 = Tag.builder().name("tag2").build();
            Set<Opinion> opinions = Set.of(
                    Opinion.builder().uuid(OPINION_UUIDS.getFirst()).qualityRating(5).serviceRating(3).priceRating(2).tags(Set.of(tag1, tag2)).build(),
                    Opinion.builder().uuid(OPINION_UUIDS.getLast()).qualityRating(4).serviceRating(5).priceRating(5).tags(Set.of(tag2)).build()
            );
            Stand stand = Stand.builder().opinions(opinions).build();
            AverageRatingDto expectedAverageRatings = new AverageRatingDto(4.5, 4.0, 3.5, List.of("tag2", "tag1"));

            when(standService.getStandByUuid(STAND_UUID)).thenReturn(stand);
            when(opinionRepository.findByStand(stand)).thenReturn(new ArrayList<>(opinions));

            AverageRatingDto resultRatings = opinionService.calculateAverageRatings(STAND_UUID);

            assertEquals(expectedAverageRatings, resultRatings);
        }

        @Test
        @DisplayName("should return average ratings for a stand based on all opinions")
        public void shouldCalculateAndReturnAverageRatingsAndTopTags() {
            Stand stand = Stand.builder().build();
            AverageRatingDto expectedAverageRatings = new AverageRatingDto(0.0, 0.0, 0.0, List.of());

            when(standService.getStandByUuid(STAND_UUID)).thenReturn(stand);
            when(opinionRepository.findByStand(stand)).thenReturn(List.of());

            AverageRatingDto resultRatings = opinionService.calculateAverageRatings(STAND_UUID);

            assertEquals(expectedAverageRatings, resultRatings);
        }

    }

    @Nested
    @DisplayName("adding an opinion")
    class AddOpinionTests {

        @Test
        @DisplayName("should add and return a new opinion")
        public void shouldAddOpinionAndReturnDto() {
            try (MockedStatic<OpinionMapper> opinionMapperMockedStatic = mockStatic(OpinionMapper.class)) {
                Stand stand = Stand.builder().uuid(STAND_UUID).build();
                String tagName = "tag1";
                OpinionAddForm opinionAddForm = new OpinionAddForm(STAND_UUID, new Rating(1, 2, 3), "comment", List.of(tagName));
                Tag tag = Tag.builder().name(tagName).build();
                Opinion opinion = Opinion.builder().qualityRating(1).serviceRating(2).priceRating(3).comment("comment").build();
                MultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", new byte[]{});
                List<MultipartFile> images = List.of(file);
                String filename = "example.jpg";

                when(userService.getCurrentUser()).thenReturn(user);
                when(standService.getStandByUuid(STAND_UUID)).thenReturn(stand);
                opinionMapperMockedStatic.when(() -> OpinionMapper.toOpinion(opinionAddForm)).thenReturn(opinion);
                when(tagRepository.findByName(tagName)).thenReturn(Optional.of(tag));
                when(imageService.uploadImage(file)).thenReturn(filename);
                when(opinionRepository.save(opinion)).thenReturn(opinion);
                opinionMapperMockedStatic.when(() -> OpinionMapper.toOpinionDto(opinion, imageService)).thenReturn(OpinionDto.builder().build());

                assertNotNull(opinionService.addOpinion(opinionAddForm, images));
                verify(opinionRepository).save(opinion);
                verify(imageService).uploadImage(file);
            }
        }

        @Test
        @DisplayName("should throw an exception when an invalid tag name was chosen")
        public void shouldThrowWhenTagNameNotFound() {
            try (MockedStatic<OpinionMapper> opinionMapperMockedStatic = mockStatic(OpinionMapper.class)) {
                Stand stand = Stand.builder().uuid(STAND_UUID).build();
                String tagName = "invalid_tag_name";
                OpinionAddForm opinionAddForm = new OpinionAddForm(STAND_UUID, new Rating(1, 2, 3), "comment", List.of(tagName));
                Opinion opinion = Opinion.builder()
                        .qualityRating(1)
                        .serviceRating(2)
                        .priceRating(3)
                        .comment("comment")
                        .build();

                when(userService.getCurrentUser()).thenReturn(user);
                when(standService.getStandByUuid(STAND_UUID)).thenReturn(stand);
                opinionMapperMockedStatic.when(() -> OpinionMapper.toOpinion(opinionAddForm)).thenReturn(opinion);
                when(tagRepository.findByName(tagName)).thenReturn(Optional.empty());

                assertThrows(ObjectNotFoundException.class, () -> opinionService.addOpinion(opinionAddForm, List.of()));
            }
        }

    }

    @Nested
    @DisplayName("deleting an opinion")
    class DeleteOpinionTests {

        @Test
        @DisplayName("should delete an existing opinion by uuid")
        public void shouldDeleteOpinionByUuid() {
            Opinion opinion = Opinion.builder().uuid(OPINION_UUID).user(user).build();

            when(userService.getCurrentUser()).thenReturn(user);
            when(opinionRepository.findByUuid(OPINION_UUID)).thenReturn(Optional.of(opinion));

            opinionService.deleteOpinion(OPINION_UUID);

            verify(opinionRepository).delete(opinion);
        }

        @Test
        @DisplayName("should throw an exception when no opinion with a specified uuid was found")
        public void shouldThrowWhenOpinionWithUuidNotFound() {
            when(userService.getCurrentUser()).thenReturn(user);
            doThrow(new ObjectNotFoundException("Opinion with uuid " + OPINION_UUID + " not found")).when(opinionRepository).findByUuid(OPINION_UUID);

            assertThrows(ObjectNotFoundException.class, () -> opinionService.deleteOpinion(OPINION_UUID));
        }

        @Test
        @DisplayName("should throw an exception when the opinion was not written by the current user")
        public void shouldThrowWhenOpinionDoesNotBelongToUser() {
            Opinion opinion = Opinion.builder().uuid(OPINION_UUID).user(new User("some other user")).build();

            when(userService.getCurrentUser()).thenReturn(user);
            when(opinionRepository.findByUuid(OPINION_UUID)).thenReturn(Optional.of(opinion));

            assertThrows(ForbiddenAccessException.class, () -> opinionService.deleteOpinion(OPINION_UUID));
        }

    }
}
