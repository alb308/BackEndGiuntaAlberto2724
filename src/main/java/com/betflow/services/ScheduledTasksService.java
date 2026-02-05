package com.betflow.services;

import com.betflow.entities.Identity;
import com.betflow.entities.Promotion;
import com.betflow.enums.PromotionStatus;
import com.betflow.repositories.IdentityRepository;
import com.betflow.repositories.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Servizio per task schedulati che inviano notifiche automatiche
 * - Daily summary alle 9:00
 * - Check promozioni in scadenza ogni 6 ore
 * - Check documenti in scadenza ogni giorno alle 8:00
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasksService {

    private final NotificationService notificationService;
    private final IdentityRepository identityRepository;
    private final PromotionRepository promotionRepository;

    /**
     * Invia un riepilogo giornaliero alle 9:00 ogni giorno
     * Cron: secondo minuto ora giorno mese giorno-settimana
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailySummary() {
        log.info("Running scheduled task: Daily Summary");

        try {
            LocalDate today = LocalDate.now();
            LocalDate sevenDaysLater = today.plusDays(7);

            // Count expiring documents (next 7 days)
            List<Identity> expiringDocuments = identityRepository
                    .findByDocumentExpiryDateBetween(today, sevenDaysLater);

            // Count promotions expiring today
            List<Promotion> expiringPromotions = promotionRepository
                    .findByDeadlineDate(today);

            notificationService.sendDailySummary(
                    expiringDocuments.size(),
                    expiringPromotions.size()
            );

            log.info("Daily summary sent: {} expiring documents, {} expiring promotions",
                    expiringDocuments.size(), expiringPromotions.size());

        } catch (Exception e) {
            log.error("Failed to send daily summary: {}", e.getMessage());
        }
    }

    /**
     * Controlla documenti in scadenza ogni giorno alle 8:00
     * Invia alert individuali per ogni documento che scade entro 7 giorni
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void checkExpiringDocuments() {
        log.info("Running scheduled task: Check Expiring Documents");

        try {
            LocalDate today = LocalDate.now();
            LocalDate sevenDaysLater = today.plusDays(7);

            List<Identity> expiringIdentities = identityRepository
                    .findByDocumentExpiryDateBetween(today, sevenDaysLater);

            for (Identity identity : expiringIdentities) {
                notificationService.sendDocumentExpiryAlert(
                        identity.getFullName(),
                        identity.getFiscalCode(),
                        identity.getDocumentExpiryDate().toString()
                );

                // Small delay to avoid Telegram rate limiting
                Thread.sleep(500);
            }

            log.info("Document expiry check completed: {} alerts sent", expiringIdentities.size());

        } catch (Exception e) {
            log.error("Failed to check expiring documents: {}", e.getMessage());
        }
    }

    /**
     * Controlla promozioni in scadenza ogni 6 ore
     * Invia alert per promozioni che scadono entro 3 giorni
     */
    @Scheduled(cron = "0 0 */6 * * *")
    public void checkExpiringPromotions() {
        log.info("Running scheduled task: Check Expiring Promotions");

        try {
            LocalDate threeDaysLater = LocalDate.now().plusDays(3);

            List<Promotion> expiringPromotions = promotionRepository
                    .findActivePromotionsExpiringWithinDays(PromotionStatus.ACTIVE, threeDaysLater);

            for (Promotion promotion : expiringPromotions) {
                notificationService.sendPromotionExpiryAlert(
                        promotion.getDescription(),
                        promotion.getAccount().getUsername(),
                        promotion.getAccount().getPlatform().getName(),
                        promotion.getDeadlineDate().toString()
                );

                // Small delay to avoid Telegram rate limiting
                Thread.sleep(500);
            }

            log.info("Promotion expiry check completed: {} alerts sent", expiringPromotions.size());

        } catch (Exception e) {
            log.error("Failed to check expiring promotions: {}", e.getMessage());
        }
    }

    /**
     * Aggiorna automaticamente lo stato delle promozioni scadute ogni giorno a mezzanotte
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void updateExpiredPromotions() {
        log.info("Running scheduled task: Update Expired Promotions");

        try {
            LocalDate today = LocalDate.now();

            List<Promotion> activePromotions = promotionRepository.findByStatus(PromotionStatus.ACTIVE);
            int expiredCount = 0;

            for (Promotion promotion : activePromotions) {
                if (promotion.getDeadlineDate() != null && promotion.getDeadlineDate().isBefore(today)) {
                    promotion.setStatus(PromotionStatus.EXPIRED);
                    promotionRepository.save(promotion);
                    expiredCount++;
                }
            }

            log.info("Expired promotions update completed: {} promotions marked as expired", expiredCount);

        } catch (Exception e) {
            log.error("Failed to update expired promotions: {}", e.getMessage());
        }
    }
}
