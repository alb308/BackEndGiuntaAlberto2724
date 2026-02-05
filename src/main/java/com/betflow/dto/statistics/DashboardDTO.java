package com.betflow.dto.statistics;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {
    private int totalIdentities;
    private int totalAccounts;
    private int activeAccounts;
    private int limitedAccounts;
    private int totalPlatforms;

    private BigDecimal totalDeposits;
    private BigDecimal totalWithdrawals;
    private BigDecimal totalCurrentBalance;
    private BigDecimal overallNetProfit;

    private long activePromotions;
    private long completedPromotions;
    private long expiredPromotions;

    private int expiringDocumentsCount;
    private int expiringPromotionsCount;

    private List<IdentityProfitDTO> topIdentitiesByProfit;
}
