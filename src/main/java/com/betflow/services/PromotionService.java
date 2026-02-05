package com.betflow.services;

import com.betflow.dto.promotion.PromotionDTO;
import com.betflow.entities.Account;
import com.betflow.entities.Promotion;
import com.betflow.enums.PromotionStatus;
import com.betflow.exceptions.ResourceNotFoundException;
import com.betflow.repositories.AccountRepository;
import com.betflow.repositories.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final AccountRepository accountRepository;

    public List<PromotionDTO> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PromotionDTO getPromotionById(UUID id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));
        return mapToDTO(promotion);
    }

    public List<PromotionDTO> getPromotionsByAccount(UUID accountId) {
        return promotionRepository.findByAccountId(accountId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PromotionDTO> getPromotionsByStatus(PromotionStatus status) {
        return promotionRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PromotionDTO> getActivePromotionsExpiringWithinDays(int days) {
        LocalDate deadlineDate = LocalDate.now().plusDays(days);
        return promotionRepository.findActivePromotionsExpiringWithinDays(PromotionStatus.ACTIVE, deadlineDate).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PromotionDTO> getPromotionsByIdentity(UUID identityId) {
        return promotionRepository.findByIdentityId(identityId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PromotionDTO createPromotion(PromotionDTO dto) {
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", dto.getAccountId()));

        Promotion promotion = Promotion.builder()
                .description(dto.getDescription())
                .bonusAmount(dto.getBonusAmount())
                .rolloverTarget(dto.getRolloverTarget())
                .rolloverDone(BigDecimal.ZERO)
                .deadlineDate(dto.getDeadlineDate())
                .status(dto.getStatus() != null ? dto.getStatus() : PromotionStatus.PENDING)
                .account(account)
                .build();

        Promotion savedPromotion = promotionRepository.save(promotion);
        log.info("Promotion created: {} for account {}", savedPromotion.getDescription(), account.getUsername());
        return mapToDTO(savedPromotion);
    }

    @Transactional
    public PromotionDTO updatePromotion(UUID id, PromotionDTO dto) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));

        if (dto.getDescription() != null) {
            promotion.setDescription(dto.getDescription());
        }

        if (dto.getBonusAmount() != null) {
            promotion.setBonusAmount(dto.getBonusAmount());
        }

        if (dto.getRolloverTarget() != null) {
            promotion.setRolloverTarget(dto.getRolloverTarget());
        }

        if (dto.getRolloverDone() != null) {
            promotion.setRolloverDone(dto.getRolloverDone());
            checkAndUpdateStatus(promotion);
        }

        if (dto.getDeadlineDate() != null) {
            promotion.setDeadlineDate(dto.getDeadlineDate());
        }

        if (dto.getStatus() != null) {
            promotion.setStatus(dto.getStatus());
        }

        Promotion savedPromotion = promotionRepository.save(promotion);
        log.info("Promotion updated: {}", savedPromotion.getId());
        return mapToDTO(savedPromotion);
    }

    @Transactional
    public PromotionDTO updateRollover(UUID id, BigDecimal rolloverAmount) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));

        BigDecimal newRolloverDone = promotion.getRolloverDone().add(rolloverAmount);
        promotion.setRolloverDone(newRolloverDone);
        checkAndUpdateStatus(promotion);

        Promotion savedPromotion = promotionRepository.save(promotion);
        log.info("Rollover updated for promotion {}: {} / {}",
                savedPromotion.getId(), newRolloverDone, savedPromotion.getRolloverTarget());
        return mapToDTO(savedPromotion);
    }

    @Transactional
    public void deletePromotion(UUID id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));

        promotionRepository.delete(promotion);
        log.info("Promotion deleted: {}", promotion.getDescription());
    }

    private void checkAndUpdateStatus(Promotion promotion) {
        if (promotion.getRolloverTarget() != null &&
            promotion.getRolloverDone().compareTo(promotion.getRolloverTarget()) >= 0) {
            promotion.setStatus(PromotionStatus.COMPLETED);
        }
    }

    private PromotionDTO mapToDTO(Promotion promotion) {
        return PromotionDTO.builder()
                .id(promotion.getId())
                .description(promotion.getDescription())
                .bonusAmount(promotion.getBonusAmount())
                .rolloverTarget(promotion.getRolloverTarget())
                .rolloverDone(promotion.getRolloverDone())
                .rolloverPercentage(promotion.getRolloverPercentage())
                .deadlineDate(promotion.getDeadlineDate())
                .status(promotion.getStatus())
                .accountId(promotion.getAccount().getId())
                .accountUsername(promotion.getAccount().getUsername())
                .platformName(promotion.getAccount().getPlatform().getName())
                .build();
    }
}
