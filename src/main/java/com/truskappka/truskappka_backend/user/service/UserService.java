package com.truskappka.truskappka_backend.user.service;

import com.truskappka.truskappka_backend.common.exception.ObjectNotFoundException;
import com.truskappka.truskappka_backend.opinion.dto.OpinionDto;
import com.truskappka.truskappka_backend.opinion.entity.Opinion;
import com.truskappka.truskappka_backend.opinion.repository.OpinionRepository;
import com.truskappka.truskappka_backend.opinion.utils.OpinionMapper;
import com.truskappka.truskappka_backend.user.dto.IsVendorDto;
import com.truskappka.truskappka_backend.user.entity.User;
import com.truskappka.truskappka_backend.user.exception.UserNotVendorException;
import com.truskappka.truskappka_backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OpinionRepository opinionRepository;

    public User getCurrentUser() {
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        return getUserByUuid(uuid);
    }

    @Transactional
    public void setUserAsVendor() {
        User user = getCurrentUser();
        user.setVendor(true);
    }

    private User getUserByUuid(UUID uuid) {
        return userRepository.findByUuid(uuid).orElseThrow(
                () -> new ObjectNotFoundException("User with uuid " + uuid + " not found")
        );
    }

    public void validateUserIsVendor(User user) {
        if (!user.isVendor()) {
            throw new UserNotVendorException("User has no access to create stands");
        }
    }

    public IsVendorDto isVendor() {
        User user = getCurrentUser();
        boolean isVendor = user.isVendor();
        return new IsVendorDto(isVendor);
    }

    public List<OpinionDto> getUserOpinions() {
        User user = getCurrentUser();
        List<Opinion> userOpinions = opinionRepository.findByUser(user);

        return userOpinions.stream()
                .map(OpinionMapper::toOpinionDto)
                .toList();
    }
}
