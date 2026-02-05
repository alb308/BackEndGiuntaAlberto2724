package com.betflow.services;

import com.betflow.dto.platform.PlatformDTO;
import com.betflow.entities.Platform;
import com.betflow.enums.PlatformType;
import com.betflow.exceptions.DuplicateResourceException;
import com.betflow.exceptions.ResourceNotFoundException;
import com.betflow.repositories.PlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlatformService {

    private final PlatformRepository platformRepository;

    public List<PlatformDTO> getAllPlatforms() {
        return platformRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PlatformDTO getPlatformById(UUID id) {
        Platform platform = platformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Platform", "id", id));
        return mapToDTO(platform);
    }

    public List<PlatformDTO> getPlatformsByType(PlatformType type) {
        return platformRepository.findByType(type).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PlatformDTO> searchPlatforms(String name) {
        return platformRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlatformDTO createPlatform(PlatformDTO dto) {
        if (platformRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Platform", "name", dto.getName());
        }

        Platform platform = Platform.builder()
                .name(dto.getName())
                .websiteUrl(dto.getWebsiteUrl())
                .type(dto.getType())
                .build();

        Platform savedPlatform = platformRepository.save(platform);
        log.info("Platform created: {}", savedPlatform.getName());
        return mapToDTO(savedPlatform);
    }

    @Transactional
    public PlatformDTO updatePlatform(UUID id, PlatformDTO dto) {
        Platform platform = platformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Platform", "id", id));

        if (dto.getName() != null && !dto.getName().equals(platform.getName())) {
            if (platformRepository.existsByName(dto.getName())) {
                throw new DuplicateResourceException("Platform", "name", dto.getName());
            }
            platform.setName(dto.getName());
        }

        if (dto.getWebsiteUrl() != null) {
            platform.setWebsiteUrl(dto.getWebsiteUrl());
        }

        if (dto.getType() != null) {
            platform.setType(dto.getType());
        }

        Platform savedPlatform = platformRepository.save(platform);
        log.info("Platform updated: {}", savedPlatform.getName());
        return mapToDTO(savedPlatform);
    }

    @Transactional
    public void deletePlatform(UUID id) {
        Platform platform = platformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Platform", "id", id));

        platformRepository.delete(platform);
        log.info("Platform deleted: {}", platform.getName());
    }

    private PlatformDTO mapToDTO(Platform platform) {
        return PlatformDTO.builder()
                .id(platform.getId())
                .name(platform.getName())
                .websiteUrl(platform.getWebsiteUrl())
                .type(platform.getType())
                .accountsCount(platform.getAccounts() != null ? platform.getAccounts().size() : 0)
                .build();
    }
}
