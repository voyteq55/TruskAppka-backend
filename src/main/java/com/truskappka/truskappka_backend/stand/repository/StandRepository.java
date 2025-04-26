package com.truskappka.truskappka_backend.stand.repository;

import com.truskappka.truskappka_backend.stand.entity.Stand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StandRepository extends JpaRepository<Stand, Long> {

    Optional<Stand> findByUuid(UUID uuid);

    boolean existsByName(String name);

    void deleteByUuid(UUID uuid);
}
