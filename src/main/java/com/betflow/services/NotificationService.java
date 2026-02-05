package com.betflow.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class NotificationService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.chat-id}")
    private String chatId;

    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s&parse_mode=HTML";

    private final RestTemplate restTemplate;

    public NotificationService() {
        this.restTemplate = new RestTemplate();
    }

    public void sendAlert(String message) {
        try {
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
            String url = String.format(TELEGRAM_API_URL, botToken, chatId, encodedMessage);

            restTemplate.getForObject(url, String.class);
            log.info("Telegram notification sent successfully");

        } catch (Exception e) {
            log.error("Failed to send Telegram notification: {}", e.getMessage());
        }
    }

    public void sendDocumentExpiryAlert(String identityName, String fiscalCode, String expiryDate) {
        String message = String.format(
                "⚠️ <b>DOCUMENT EXPIRY ALERT</b>\n\n" +
                "📋 Identity: <b>%s</b>\n" +
                "🆔 Fiscal Code: %s\n" +
                "📅 Expiry Date: <b>%s</b>\n\n" +
                "Please renew the document as soon as possible!",
                identityName, fiscalCode, expiryDate
        );
        sendAlert(message);
    }

    public void sendPromotionExpiryAlert(String promotionDesc, String accountUsername, String platformName, String deadlineDate) {
        String message = String.format(
                "🎁 <b>PROMOTION EXPIRY ALERT</b>\n\n" +
                "📝 Promotion: <b>%s</b>\n" +
                "👤 Account: %s\n" +
                "🎰 Platform: %s\n" +
                "⏰ Deadline: <b>%s</b>\n\n" +
                "Complete the rollover before the deadline!",
                promotionDesc, accountUsername, platformName, deadlineDate
        );
        sendAlert(message);
    }

    public void sendWelcomeMessage(String username) {
        String message = String.format(
                "👋 <b>Welcome to BetFlow Manager!</b>\n\n" +
                "User <b>%s</b> has been successfully registered.\n" +
                "Start managing your matched betting operations now!",
                username
        );
        sendAlert(message);
    }

    public void sendDailySummary(int expiringDocuments, int expiringPromotions) {
        if (expiringDocuments == 0 && expiringPromotions == 0) {
            return;
        }

        String message = String.format(
                "📊 <b>DAILY SUMMARY</b>\n\n" +
                "📄 Documents expiring in 7 days: <b>%d</b>\n" +
                "🎁 Promotions expiring today: <b>%d</b>\n\n" +
                "Check the dashboard for details!",
                expiringDocuments, expiringPromotions
        );
        sendAlert(message);
    }
}
