package com.betflow.services;

import com.betflow.dto.statistics.DashboardDTO;
import com.betflow.dto.statistics.IdentityProfitDTO;
import com.betflow.entities.Identity;
import com.betflow.enums.PromotionStatus;
import com.betflow.exceptions.ResourceNotFoundException;
import com.betflow.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    private final IdentityRepository identityRepository;
    private final AccountRepository accountRepository;
    private final PlatformRepository platformRepository;
    private final PromotionRepository promotionRepository;
    private final FinancialOperationRepository financialOperationRepository;

    /**
     * Calcola le statistiche di profitto per una singola identità
     */
    public IdentityProfitDTO calculateProfitByIdentity(UUID identityId) {
        Identity identity = identityRepository.findById(identityId)
                .orElseThrow(() -> new ResourceNotFoundException("Identity", "id", identityId));

        BigDecimal totalDeposits = financialOperationRepository.sumDepositsByIdentityId(identityId);
        BigDecimal totalWithdrawals = financialOperationRepository.sumWithdrawalsByIdentityId(identityId);
        BigDecimal totalBalance = accountRepository.sumCurrentBalanceByIdentityId(identityId);

        if (totalDeposits == null) totalDeposits = BigDecimal.ZERO;
        if (totalWithdrawals == null) totalWithdrawals = BigDecimal.ZERO;
        if (totalBalance == null) totalBalance = BigDecimal.ZERO;

        // Profit = (Withdrawals + Current Balance) - Deposits
        BigDecimal netProfit = totalWithdrawals.add(totalBalance).subtract(totalDeposits);

        int accountsCount = accountRepository.findByIdentityId(identityId).size();

        return IdentityProfitDTO.builder()
                .identityId(identityId)
                .identityFullName(identity.getFullName())
                .totalDeposits(totalDeposits)
                .totalWithdrawals(totalWithdrawals)
                .totalCurrentBalance(totalBalance)
                .netProfit(netProfit)
                .totalAccounts(accountsCount)
                .build();
    }

    /**
     * Calcola le statistiche aggregate per la dashboard
     */
    public DashboardDTO getDashboardStatistics() {
        log.info("Calculating dashboard statistics");

        // Count entities
        int totalIdentities = (int) identityRepository.count();
        int totalAccounts = (int) accountRepository.count();
        int activeAccounts = (int) accountRepository.countActiveAccounts();
        int limitedAccounts = (int) accountRepository.countLimitedAccounts();
        int totalPlatforms = (int) platformRepository.count();

        // Financial totals
        BigDecimal totalDeposits = financialOperationRepository.sumAllDeposits();
        BigDecimal totalWithdrawals = financialOperationRepository.sumAllWithdrawals();
        BigDecimal totalCurrentBalance = accountRepository.sumAllCurrentBalances();

        if (totalDeposits == null) totalDeposits = BigDecimal.ZERO;
        if (totalWithdrawals == null) totalWithdrawals = BigDecimal.ZERO;
        if (totalCurrentBalance == null) totalCurrentBalance = BigDecimal.ZERO;

        // Net profit
        BigDecimal overallNetProfit = totalWithdrawals.add(totalCurrentBalance).subtract(totalDeposits);

        // Promotions count by status
        long activePromotions = promotionRepository.countByStatus(PromotionStatus.ACTIVE);
        long completedPromotions = promotionRepository.countByStatus(PromotionStatus.COMPLETED);
        long expiredPromotions = promotionRepository.countByStatus(PromotionStatus.EXPIRED);

        // Expiring documents (next 7 days)
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);
        int expiringDocumentsCount = identityRepository.findByDocumentExpiryDateBetween(today, sevenDaysLater).size();

        // Expiring promotions (next 7 days)
        int expiringPromotionsCount = promotionRepository
                .findActivePromotionsExpiringWithinDays(PromotionStatus.ACTIVE, sevenDaysLater).size();

        // Top 5 identities by profit
        List<IdentityProfitDTO> topIdentitiesByProfit = identityRepository.findAll().stream()
                .map(identity -> calculateProfitByIdentity(identity.getId()))
                .sorted(Comparator.comparing(IdentityProfitDTO::getNetProfit).reversed())
                .limit(5)
                .collect(Collectors.toList());

        return DashboardDTO.builder()
                .totalIdentities(totalIdentities)
                .totalAccounts(totalAccounts)
                .activeAccounts(activeAccounts)
                .limitedAccounts(limitedAccounts)
                .totalPlatforms(totalPlatforms)
                .totalDeposits(totalDeposits)
                .totalWithdrawals(totalWithdrawals)
                .totalCurrentBalance(totalCurrentBalance)
                .overallNetProfit(overallNetProfit)
                .activePromotions(activePromotions)
                .completedPromotions(completedPromotions)
                .expiredPromotions(expiredPromotions)
                .expiringDocumentsCount(expiringDocumentsCount)
                .expiringPromotionsCount(expiringPromotionsCount)
                .topIdentitiesByProfit(topIdentitiesByProfit)
                .build();
    }

    /**
     * Ottiene le statistiche di profitto per tutte le identità
     */
    public List<IdentityProfitDTO> getAllIdentitiesProfits() {
        return identityRepository.findAll().stream()
                .map(identity -> calculateProfitByIdentity(identity.getId()))
                .sorted(Comparator.comparing(IdentityProfitDTO::getNetProfit).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Ottiene le identità con profitto positivo
     */
    public List<IdentityProfitDTO> getProfitableIdentities() {
        return identityRepository.findAll().stream()
                .map(identity -> calculateProfitByIdentity(identity.getId()))
                .filter(profit -> profit.getNetProfit().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(IdentityProfitDTO::getNetProfit).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Ottiene le identità in perdita
     */
    public List<IdentityProfitDTO> getUnprofitableIdentities() {
        return identityRepository.findAll().stream()
                .map(identity -> calculateProfitByIdentity(identity.getId()))
                .filter(profit -> profit.getNetProfit().compareTo(BigDecimal.ZERO) < 0)
                .sorted(Comparator.comparing(IdentityProfitDTO::getNetProfit))
                .collect(Collectors.toList());
    }
}
