package com.truskappka.truskappka_backend.user.service;

import com.truskappka.truskappka_backend.common.exception.ObjectNotFoundException;
import com.truskappka.truskappka_backend.user.entity.User;
import com.truskappka.truskappka_backend.user.exception.UserNotVendorException;
import com.truskappka.truskappka_backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        return getUserByUuid(uuid);
    }

    @Transactional
    public void setUserAsVendor(UUID userUuid) {
        User user = getUserByUuid(userUuid);
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
}
