package com.betflow.controllers;

import com.betflow.dto.promotion.PromotionDTO;
import com.betflow.enums.PromotionStatus;
import com.betflow.services.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    public ResponseEntity<List<PromotionDTO>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionDTO> getPromotionById(@PathVariable UUID id) {
        return ResponseEntity.ok(promotionService.getPromotionById(id));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<PromotionDTO>> getPromotionsByAccount(@PathVariable UUID accountId) {
        return ResponseEntity.ok(promotionService.getPromotionsByAccount(accountId));
    }

    @GetMapping("/identity/{identityId}")
    public ResponseEntity<List<PromotionDTO>> getPromotionsByIdentity(@PathVariable UUID identityId) {
        return ResponseEntity.ok(promotionService.getPromotionsByIdentity(identityId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PromotionDTO>> getPromotionsByStatus(@PathVariable PromotionStatus status) {
        return ResponseEntity.ok(promotionService.getPromotionsByStatus(status));
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<PromotionDTO>> getActivePromotionsExpiringWithinDays(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(promotionService.getActivePromotionsExpiringWithinDays(days));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PromotionDTO> createPromotion(@RequestBody @Valid PromotionDTO dto) {
        PromotionDTO created = promotionService.createPromotion(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PromotionDTO> updatePromotion(
            @PathVariable UUID id,
            @RequestBody @Valid PromotionDTO dto) {
        return ResponseEntity.ok(promotionService.updatePromotion(id, dto));
    }

    @PatchMapping("/{id}/rollover")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PromotionDTO> updateRollover(
            @PathVariable UUID id,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(promotionService.updateRollover(id, amount));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deletePromotion(@PathVariable UUID id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }
}
