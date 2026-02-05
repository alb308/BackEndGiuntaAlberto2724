package com.betflow.repositories;

import com.betflow.entities.Platform;
import com.betflow.enums.PlatformType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, UUID> {

    Optional<Platform> findByName(String name);

    boolean existsByName(String name);

    List<Platform> findByType(PlatformType type);

    List<Platform> findByNameContainingIgnoreCase(String name);
}
