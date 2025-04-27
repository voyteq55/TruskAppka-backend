package com.truskappka.truskappka_backend.opinion.service;

import com.truskappka.truskappka_backend.common.exception.ForbiddenAccessException;
import com.truskappka.truskappka_backend.common.exception.ObjectNotFoundException;
import com.truskappka.truskappka_backend.opinion.dto.AverageRatingDto;
import com.truskappka.truskappka_backend.opinion.dto.OpinionAddForm;
import com.truskappka.truskappka_backend.opinion.dto.OpinionDto;
import com.truskappka.truskappka_backend.opinion.dto.OpinionEditForm;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
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

    public List<OpinionDto> getOpinionsForStand(UUID standUuid) {
        Stand stand = standService.getStandByUuid(standUuid);

        List<Opinion> opinions = opinionRepository.findByStand(stand);

        return opinions.stream()
                .map(OpinionMapper::toOpinionDto)
                .toList();
    }

    public AverageRatingDto calculateAverageRatings(UUID standUuid) {
        Stand stand = standService.getStandByUuid(standUuid);

        List<Opinion> opinions = opinionRepository.findByStand(stand);

        if (opinions.isEmpty()) {
            return new AverageRatingDto(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        }

        BigDecimal totalQuality = BigDecimal.ZERO;
        BigDecimal totalService = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Opinion opinion : opinions) {
            totalQuality = totalQuality.add(opinion.getQualityRating());
            totalService = totalService.add(opinion.getServiceRating());
            totalPrice = totalPrice.add(opinion.getPriceRating());
        }

        BigDecimal averageQuality = totalQuality.divide(BigDecimal.valueOf(opinions.size()), RoundingMode.HALF_UP);
        BigDecimal averageService = totalService.divide(BigDecimal.valueOf(opinions.size()), RoundingMode.HALF_UP);
        BigDecimal averagePrice = totalPrice.divide(BigDecimal.valueOf(opinions.size()), RoundingMode.HALF_UP);

        return new AverageRatingDto(averageQuality, averageService, averagePrice);
    }

    @Transactional
    public OpinionDto addOpinion(OpinionAddForm opinionAddForm) {
        User user = userService.getCurrentUser();
        Stand stand = standService.getStandByUuid(opinionAddForm.standUuid());

        Opinion opinion = OpinionMapper.toOpinion(opinionAddForm);
        opinion.setUser(user);
        opinion.setStand(stand);
        opinion.setTags(getTagsFromNames(opinionAddForm.tagNames()));

        Opinion savedOpinion = opinionRepository.save(opinion);
        return OpinionMapper.toOpinionDto(savedOpinion);
    }

    private Set<Tag> getTagsFromNames(List<String> tagNames) {
        return tagNames.stream()
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElseThrow(() -> new ObjectNotFoundException("Tag with name " + tagName + " not found")))
                .collect(Collectors.toSet());
    }

    @Transactional
    public OpinionDto updateOpinion(UUID opinionUuid, OpinionEditForm opinionEditForm) {
        User user = userService.getCurrentUser();
        Opinion opinion = getOpinionByUuid(opinionUuid);

        validateOpinionOwnership(opinion, user);

        Optional.ofNullable(opinionEditForm.rating()).ifPresent(rating -> {
            Optional.ofNullable(rating.quality()).ifPresent(opinion::setQualityRating);
            Optional.ofNullable(rating.service()).ifPresent(opinion::setServiceRating);
            Optional.ofNullable(rating.price()).ifPresent(opinion::setPriceRating);
        });

        Optional.ofNullable(opinionEditForm.comment()).ifPresent(opinion::setComment);

        opinion.setTags(getTagsFromNames(opinionEditForm.tagNames()));

        return OpinionMapper.toOpinionDto(opinion);
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
