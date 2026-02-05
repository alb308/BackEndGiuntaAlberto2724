package com.betflow.graphql;

import com.betflow.dto.statistics.DashboardDTO;
import com.betflow.dto.statistics.IdentityProfitDTO;
import com.betflow.entities.*;
import com.betflow.enums.PlatformType;
import com.betflow.enums.PromotionStatus;
import com.betflow.enums.UserRole;
import com.betflow.repositories.*;
import com.betflow.services.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * GraphQL Query Controller
 * Gestisce tutte le query GraphQL per il sistema BetFlow Manager
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class QueryController {

    private final IdentityRepository identityRepository;
    private final PlatformRepository platformRepository;
    private final AccountRepository accountRepository;
    private final PromotionRepository promotionRepository;
    private final FinancialOperationRepository financialOperationRepository;
    private final UserRepository userRepository;
    private final StatisticsService statisticsService;

    // ==================== IDENTITY QUERIES ====================

    @QueryMapping
    public List<Identity> identities() {
        log.debug("GraphQL query: identities");
        return identityRepository.findAll();
    }

    @QueryMapping
    public Identity identity(@Argument UUID id) {
        log.debug("GraphQL query: identity({})", id);
        return identityRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Identity> identitiesByManager(@Argument UUID managerId) {
        log.debug("GraphQL query: identitiesByManager({})", managerId);
        return identityRepository.findByManagerId(managerId);
    }

    @QueryMapping
    public List<Identity> expiringDocuments(@Argument int days) {
        log.debug("GraphQL query: expiringDocuments({})", days);
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        return identityRepository.findByDocumentExpiryDateBetween(today, endDate);
    }

    // ==================== PLATFORM QUERIES ====================

    @QueryMapping
    public List<Platform> platforms() {
        log.debug("GraphQL query: platforms");
        return platformRepository.findAll();
    }

    @QueryMapping
    public Platform platform(@Argument UUID id) {
        log.debug("GraphQL query: platform({})", id);
        return platformRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Platform> platformsByType(@Argument PlatformType type) {
        log.debug("GraphQL query: platformsByType({})", type);
        return platformRepository.findByType(type);
    }

    @QueryMapping
    public List<Platform> searchPlatforms(@Argument String name) {
        log.debug("GraphQL query: searchPlatforms({})", name);
        return platformRepository.findByNameContainingIgnoreCase(name);
    }

    // ==================== ACCOUNT QUERIES ====================

    @QueryMapping
    public List<Account> accounts() {
        log.debug("GraphQL query: accounts");
        return accountRepository.findAll();
    }

    @QueryMapping
    public Account account(@Argument UUID id) {
        log.debug("GraphQL query: account({})", id);
        return accountRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Account> accountsByIdentity(@Argument UUID identityId) {
        log.debug("GraphQL query: accountsByIdentity({})", identityId);
        return accountRepository.findByIdentityId(identityId);
    }

    @QueryMapping
    public List<Account> accountsByPlatform(@Argument UUID platformId) {
        log.debug("GraphQL query: accountsByPlatform({})", platformId);
        return accountRepository.findByPlatformId(platformId);
    }

    @QueryMapping
    public List<Account> activeAccounts() {
        log.debug("GraphQL query: activeAccounts");
        return accountRepository.findByIsActiveTrue();
    }

    @QueryMapping
    public List<Account> limitedAccounts() {
        log.debug("GraphQL query: limitedAccounts");
        return accountRepository.findByIsLimitedTrue();
    }

    // ==================== PROMOTION QUERIES ====================

    @QueryMapping
    public List<Promotion> promotions() {
        log.debug("GraphQL query: promotions");
        return promotionRepository.findAll();
    }

    @QueryMapping
    public Promotion promotion(@Argument UUID id) {
        log.debug("GraphQL query: promotion({})", id);
        return promotionRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Promotion> promotionsByAccount(@Argument UUID accountId) {
        log.debug("GraphQL query: promotionsByAccount({})", accountId);
        return promotionRepository.findByAccountId(accountId);
    }

    @QueryMapping
    public List<Promotion> promotionsByStatus(@Argument PromotionStatus status) {
        log.debug("GraphQL query: promotionsByStatus({})", status);
        return promotionRepository.findByStatus(status);
    }

    @QueryMapping
    public List<Promotion> expiringPromotions(@Argument int days) {
        log.debug("GraphQL query: expiringPromotions({})", days);
        LocalDate deadlineDate = LocalDate.now().plusDays(days);
        return promotionRepository.findActivePromotionsExpiringWithinDays(PromotionStatus.ACTIVE, deadlineDate);
    }

    // ==================== FINANCIAL OPERATION QUERIES ====================

    @QueryMapping
    public List<Deposit> deposits() {
        log.debug("GraphQL query: deposits");
        return financialOperationRepository.findAllDeposits();
    }

    @QueryMapping
    public List<Deposit> depositsByAccount(@Argument UUID accountId) {
        log.debug("GraphQL query: depositsByAccount({})", accountId);
        return financialOperationRepository.findDepositsByAccountId(accountId);
    }

    @QueryMapping
    public List<Withdrawal> withdrawals() {
        log.debug("GraphQL query: withdrawals");
        return financialOperationRepository.findAllWithdrawals();
    }

    @QueryMapping
    public List<Withdrawal> withdrawalsByAccount(@Argument UUID accountId) {
        log.debug("GraphQL query: withdrawalsByAccount({})", accountId);
        return financialOperationRepository.findWithdrawalsByAccountId(accountId);
    }

    @QueryMapping
    public List<Withdrawal> pendingWithdrawals() {
        log.debug("GraphQL query: pendingWithdrawals");
        return financialOperationRepository.findWithdrawalsByStatus(
                com.betflow.enums.WithdrawalStatus.REQUESTED
        );
    }

    @QueryMapping
    public List<BetOperation> bets() {
        log.debug("GraphQL query: bets");
        return financialOperationRepository.findAllBetOperations();
    }

    @QueryMapping
    public List<BetOperation> betsByAccount(@Argument UUID accountId) {
        log.debug("GraphQL query: betsByAccount({})", accountId);
        return financialOperationRepository.findBetOperationsByAccountId(accountId);
    }

    @QueryMapping
    public List<BetOperation> pendingBets() {
        log.debug("GraphQL query: pendingBets");
        return financialOperationRepository.findPendingBets();
    }

    // ==================== STATISTICS QUERIES ====================

    @QueryMapping
    public DashboardDTO dashboard() {
        log.debug("GraphQL query: dashboard");
        return statisticsService.getDashboardStatistics();
    }

    @QueryMapping
    public IdentityProfitDTO identityProfit(@Argument UUID identityId) {
        log.debug("GraphQL query: identityProfit({})", identityId);
        return statisticsService.calculateProfitByIdentity(identityId);
    }

    @QueryMapping
    public List<IdentityProfitDTO> allIdentityProfits() {
        log.debug("GraphQL query: allIdentityProfits");
        return statisticsService.getAllIdentitiesProfits();
    }

    @QueryMapping
    public List<IdentityProfitDTO> profitableIdentities() {
        log.debug("GraphQL query: profitableIdentities");
        return statisticsService.getProfitableIdentities();
    }

    @QueryMapping
    public List<IdentityProfitDTO> unprofitableIdentities() {
        log.debug("GraphQL query: unprofitableIdentities");
        return statisticsService.getUnprofitableIdentities();
    }

    // ==================== USER QUERIES ====================

    @QueryMapping
    public List<User> users() {
        log.debug("GraphQL query: users");
        return userRepository.findAll();
    }

    @QueryMapping
    public User user(@Argument UUID id) {
        log.debug("GraphQL query: user({})", id);
        return userRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<User> usersByRole(@Argument UserRole role) {
        log.debug("GraphQL query: usersByRole({})", role);
        return userRepository.findByRole(role);
    }
}
