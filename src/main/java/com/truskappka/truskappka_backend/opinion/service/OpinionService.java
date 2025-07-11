package com.truskappka.truskappka_backend.opinion.service;

import com.truskappka.truskappka_backend.common.exception.ForbiddenAccessException;
import com.truskappka.truskappka_backend.common.exception.ObjectNotFoundException;
import com.truskappka.truskappka_backend.image.service.ImageService;
import com.truskappka.truskappka_backend.opinion.dto.AverageRatingDto;
import com.truskappka.truskappka_backend.opinion.dto.OpinionAddForm;
import com.truskappka.truskappka_backend.opinion.dto.OpinionDto;
import com.truskappka.truskappka_backend.opinion.entity.Opinion;
import com.truskappka.truskappka_backend.opinion.repository.OpinionRepository;
import com.truskappka.truskappka_backend.opinion.utils.OpinionMapper;
import com.truskappka.truskappka_backend.stand.entity.Stand;
import com.truskappka.truskappka_backend.stand.service.StandService;
import com.truskappka.truskappka_backend.tag.entity.Tag;
import com.truskappka.truskappka_backend.tag.repository.TagRepository;
import com.truskappka.truskappka_backend.user.entity.User;
import com.truskappka.truskappka_backend.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpinionService {

    private final OpinionRepository opinionRepository;
    private final UserService userService;
    private final StandService standService;
    private final TagRepository tagRepository;
    private final ImageService imageService;

    public List<OpinionDto> getOpinionsForStand(UUID standUuid) {
        Stand stand = standService.getStandByUuid(standUuid);

        List<Opinion> opinions = opinionRepository.findByStand(stand);

        return opinions.stream()
                .map(opinion -> OpinionMapper.toOpinionDto(opinion, imageService))
                .toList();
    }

    public AverageRatingDto calculateAverageRatings(UUID standUuid) {
        Stand stand = standService.getStandByUuid(standUuid);

        List<Opinion> opinions = opinionRepository.findByStand(stand);

        if (opinions.isEmpty()) {
            return new AverageRatingDto(0.0, 0.0, 0.0, List.of());
        }

        double totalQuality = 0.0;
        double totalService = 0.0;
        double totalPrice = 0.0;
        Map<String, Integer> tagFrequency = new HashMap<>();

        for (Opinion opinion : opinions) {
            totalQuality += opinion.getQualityRating();
            totalService += opinion.getServiceRating();
            totalPrice += opinion.getPriceRating();

            if (opinion.getTags() != null) {
                for (Tag tag : opinion.getTags()) {
                    tagFrequency.merge(tag.getName(), 1, Integer::sum);
                }
            }
        }

        int size = opinions.size();
        double averageQuality = totalQuality / size;
        double averageService = totalService / size;
        double averagePrice = totalPrice / size;

        List<String> topTags = tagFrequency.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue())) // descending
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        return new AverageRatingDto(averageQuality, averageService, averagePrice, topTags);
    }


    @Transactional
    public OpinionDto addOpinion(OpinionAddForm opinionAddForm, List<MultipartFile> images) {
        User user = userService.getCurrentUser();
        Stand stand = standService.getStandByUuid(opinionAddForm.standUuid());

        Opinion opinion = OpinionMapper.toOpinion(opinionAddForm);
        opinion.setUser(user);
        opinion.setStand(stand);
        opinion.setTags(getTagsFromNames(opinionAddForm.tagNames()));

        Set<String> imageFilenames = new HashSet<>();
        if (images != null) {
            for (MultipartFile image : images) {
                String filename = imageService.uploadImage(image);
                imageFilenames.add(filename);
            }
        }
        opinion.setImageUrls(imageFilenames);

        Opinion savedOpinion = opinionRepository.save(opinion);
        return OpinionMapper.toOpinionDto(savedOpinion, imageService);
    }

    private Set<Tag> getTagsFromNames(List<String> tagNames) {
        return tagNames.stream()
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElseThrow(() -> new ObjectNotFoundException("Tag with name " + tagName + " not found")))
                .collect(Collectors.toSet());
    }

    private void validateOpinionOwnership(Opinion opinion, User user) {
        if (!opinion.getUser().equals(user)) {
            throw new ForbiddenAccessException("You can only edit / delete your own opinions");
        }
    }

    @Transactional
    public void deleteOpinion(UUID opinionUuid) {
        User user = userService.getCurrentUser();
        Opinion opinion = getOpinionByUuid(opinionUuid);

        validateOpinionOwnership(opinion, user);

        opinionRepository.delete(opinion);
    }

    private Opinion getOpinionByUuid(UUID uuid) {
        return opinionRepository.findByUuid(uuid)
                .orElseThrow(() -> new ObjectNotFoundException("Opinion with uuid " + uuid + " not found"));
    }
}
