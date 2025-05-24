package com.truskappka.truskappka_backend.stand.repository;

import com.truskappka.truskappka_backend.stand.entity.Stand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StandRepository extends JpaRepository<Stand, Long> {

    @Query(value = """
SELECT * FROM stands
WHERE (
    6371 * acos(
        LEAST(1, GREATEST(-1,
            cos(radians(:lat)) *
            cos(radians(latitude)) *
            cos(radians(longitude) - radians(:lon)) +
            sin(radians(:lat)) *
            sin(radians(latitude))
        ))
    )
) <= :radius
""", nativeQuery = true)
    List<Stand> findAllWithinRadius(
            @Param("lat") double latitude,
            @Param("lon") double longitude,
            @Param("radius") double radiusKm
    );

    Optional<Stand> findByUuid(UUID uuid);

    Optional<Stand> findByName(String name);

    boolean existsByName(String name);

    void deleteByUuid(UUID uuid);
}
