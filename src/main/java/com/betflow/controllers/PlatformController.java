package com.betflow.controllers;

import com.betflow.dto.platform.PlatformDTO;
import com.betflow.enums.PlatformType;
import com.betflow.services.PlatformService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/platforms")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformService platformService;

    @GetMapping
    public ResponseEntity<List<PlatformDTO>> getAllPlatforms() {
        return ResponseEntity.ok(platformService.getAllPlatforms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlatformDTO> getPlatformById(@PathVariable UUID id) {
        return ResponseEntity.ok(platformService.getPlatformById(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<PlatformDTO>> getPlatformsByType(@PathVariable PlatformType type) {
        return ResponseEntity.ok(platformService.getPlatformsByType(type));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlatformDTO>> searchPlatforms(@RequestParam String name) {
        return ResponseEntity.ok(platformService.searchPlatforms(name));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlatformDTO> createPlatform(@RequestBody @Valid PlatformDTO dto) {
        PlatformDTO created = platformService.createPlatform(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlatformDTO> updatePlatform(
            @PathVariable UUID id,
            @RequestBody @Valid PlatformDTO dto) {
        return ResponseEntity.ok(platformService.updatePlatform(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlatform(@PathVariable UUID id) {
        platformService.deletePlatform(id);
        return ResponseEntity.noContent().build();
    }
}
