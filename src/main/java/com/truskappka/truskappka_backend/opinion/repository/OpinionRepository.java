package com.truskappka.truskappka_backend.opinion.repository;

import com.truskappka.truskappka_backend.opinion.entity.Opinion;
import com.truskappka.truskappka_backend.stand.entity.Stand;
import com.truskappka.truskappka_backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OpinionRepository extends JpaRepository<Opinion, Long> {

    Optional<Opinion> findByUuid(UUID uuid);
    List<Opinion> findByStand(Stand stand);
    List<Opinion> findByUser(User user);
}
