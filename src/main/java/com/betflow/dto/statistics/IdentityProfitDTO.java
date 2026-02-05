package com.betflow.dto.statistics;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentityProfitDTO {
    private UUID identityId;
    private String identityFullName;
    private BigDecimal totalDeposits;
    private BigDecimal totalWithdrawals;
    private BigDecimal totalCurrentBalance;
    private BigDecimal netProfit;
    private int totalAccounts;
}
