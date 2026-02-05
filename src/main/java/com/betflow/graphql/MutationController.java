package com.betflow.graphql;

import com.betflow.dto.account.AccountDTO;
import com.betflow.dto.identity.IdentityDTO;
import com.betflow.dto.operation.BetOperationDTO;
import com.betflow.dto.operation.DepositDTO;
import com.betflow.dto.operation.WithdrawalDTO;
import com.betflow.dto.platform.PlatformDTO;
import com.betflow.dto.promotion.PromotionDTO;
import com.betflow.entities.*;
import com.betflow.enums.BetOutcome;
import com.betflow.enums.PromotionStatus;
import com.betflow.enums.WithdrawalStatus;
import com.betflow.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * GraphQL Mutation Controller
 * Gestisce tutte le mutazioni GraphQL per il sistema BetFlow Manager
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class MutationController {

    private final IdentityService identityService;
    private final PlatformService platformService;
    private final AccountService accountService;
    private final PromotionService promotionService;
    private final FinancialOperationService financialOperationService;

    // ==================== IDENTITY MUTATIONS ====================

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public IdentityDTO createIdentity(@Argument Map<String, Object> input) {
        log.debug("GraphQL mutation: createIdentity");
        IdentityDTO dto = mapToIdentityDTO(input);
        return identityService.createIdentity(dto);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public IdentityDTO updateIdentity(@Argument UUID id, @Argument Map<String, Object> input) {
        log.debug("GraphQL mutation: updateIdentity({})", id);
        IdentityDTO dto = mapToIdentityDTO(input);
        return identityService.updateIdentity(id, dto);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Boolean deleteIdentity(@Argument UUID id) {
        log.debug("GraphQL mutation: deleteIdentity({})", id);
        identityService.deleteIdentity(id);
        return true;
    }

    // ==================== PLATFORM MUTATIONS ====================

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PlatformDTO createPlatform(@Argument Map<String, Object> input) {
        log.debug("GraphQL mutation: createPlatform");
        PlatformDTO dto = mapToPlatformDTO(input);
        return platformService.createPlatform(dto);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PlatformDTO updatePlatform(@Argument UUID id, @Argument Map<String, Object> input) {
        log.debug("GraphQL mutation: updatePlatform({})", id);
        PlatformDTO dto = mapToPlatformDTO(input);
        return platformService.updatePlatform(id, dto);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deletePlatform(@Argument UUID id) {
        log.debug("GraphQL mutation: deletePlatform({})", id);
        platformService.deletePlatform(id);
        return true;
    }

    // ==================== ACCOUNT MUTATIONS ====================

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public AccountDTO createAccount(@Argument Map<String, Object> input) {
        log.debug("GraphQL mutation: createAccount");
        AccountDTO dto = mapToAccountDTO(input);
        return accountService.createAccount(dto);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public AccountDTO updateAccount(@Argument UUID id, @Argument Map<String, Object> input) {
        log.debug("GraphQL mutation: updateAccount({})", id);
        AccountDTO dto = mapToAccountDTO(input);
        return accountService.updateAccount(id, dto);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public AccountDTO updateAccountBalance(@Argument UUID id, @Argument BigDecimal balance) {
        log.debug("GraphQL mutation: updateAccountBalance({}, {})", id, balance);
        return accountService.updateBalance(id, balance);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Boolean deleteAccount(@Argument UUID id) {
        log.debug("GraphQL mutation: deleteAccount({})", id);
        accountService.deleteAccount(id);
        return true;
    }

    // ==================== PROMOTION MUTATIONS ====================

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public PromotionDTO createPromotion(@Argument Map<String, Object> input) {
        log.debug("GraphQL mutation: createPromotion");
        PromotionDTO dto = mapToPromotionDTO(input);
        return promotionService.createPromotion(dto);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public PromotionDTO updatePromotion(@Argument UUID id, @Argument Map<String, Object> input) {
        log.debug("GraphQL mutation: updatePromotion({})", id);
        PromotionDTO dto = mapToPromotionDTO(input);
        return promotionService.updatePromotion(id, dto);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public PromotionDTO updateRollover(@Argument UUID id, @Argument BigDecimal currentRollover) {
        log.debug("GraphQL mutation: updateRollover({}, {})", id, currentRollover);
        return promotionService.updateRollover(id, currentRollover);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Boolean deletePromotion(@Argument UUID id) {
        log.debug("GraphQL mutation: deletePromotion({})", id);
        promotionService.deletePromotion(id);
        return true;
    }

    // ==================== FINANCIAL OPERATION MUTATIONS ====================

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public DepositDTO createDeposit(@Argument Map<String, Object> input) {
        log.debug("GraphQL mutation: createDeposit");
        DepositDTO dto = mapToDepositDTO(input);
        return financialOperationService.createDeposit(dto);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public WithdrawalDTO createWithdrawal(@Argument Map<String, Object> input) {
        log.debug("GraphQL mutation: createWithdrawal");
        WithdrawalDTO dto = mapToWithdrawalDTO(input);
        return financialOperationService.createWithdrawal(dto);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public WithdrawalDTO updateWithdrawalStatus(@Argument UUID id, @Argument WithdrawalStatus status) {
        log.debug("GraphQL mutation: updateWithdrawalStatus({}, {})", id, status);
        WithdrawalDTO dto = WithdrawalDTO.builder().status(status).build();
        return financialOperationService.updateWithdrawalStatus(id, dto);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public BetOperationDTO createBet(@Argument Map<String, Object> input) {
        log.debug("GraphQL mutation: createBet");
        BetOperationDTO dto = mapToBetDTO(input);
        return financialOperationService.createBetOperation(dto);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public BetOperationDTO updateBetOutcome(@Argument UUID id, @Argument BetOutcome outcome) {
        log.debug("GraphQL mutation: updateBetOutcome({}, {})", id, outcome);
        BetOperationDTO dto = BetOperationDTO.builder().outcome(outcome).build();
        return financialOperationService.updateBetOutcome(id, dto);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteOperation(@Argument UUID id) {
        log.debug("GraphQL mutation: deleteOperation({})", id);
        financialOperationService.deleteOperation(id);
        return true;
    }

    // ==================== DTO MAPPERS ====================

    private IdentityDTO mapToIdentityDTO(Map<String, Object> input) {
        IdentityDTO.IdentityDTOBuilder builder = IdentityDTO.builder();

        if (input.get("firstName") != null) builder.firstName((String) input.get("firstName"));
        if (input.get("lastName") != null) builder.lastName((String) input.get("lastName"));
        if (input.get("fiscalCode") != null) builder.fiscalCode((String) input.get("fiscalCode"));
        if (input.get("email") != null) builder.email((String) input.get("email"));
        if (input.get("phone") != null) builder.phone((String) input.get("phone"));
        if (input.get("documentExpiryDate") != null) {
            builder.documentExpiryDate(LocalDate.parse((String) input.get("documentExpiryDate")));
        }
        if (input.get("notes") != null) builder.notes((String) input.get("notes"));
        if (input.get("managerId") != null) builder.managerId(UUID.fromString((String) input.get("managerId")));

        return builder.build();
    }

    private PlatformDTO mapToPlatformDTO(Map<String, Object> input) {
        PlatformDTO.PlatformDTOBuilder builder = PlatformDTO.builder();

        if (input.get("name") != null) builder.name((String) input.get("name"));
        if (input.get("type") != null) {
            builder.type(com.betflow.enums.PlatformType.valueOf((String) input.get("type")));
        }
        if (input.get("website") != null) builder.websiteUrl((String) input.get("website"));
        if (input.get("notes") != null) builder.notes((String) input.get("notes"));

        return builder.build();
    }

    private AccountDTO mapToAccountDTO(Map<String, Object> input) {
        AccountDTO.AccountDTOBuilder builder = AccountDTO.builder();

        if (input.get("username") != null) builder.username((String) input.get("username"));
        if (input.get("email") != null) builder.email((String) input.get("email"));
        if (input.get("currentBalance") != null) {
            builder.currentBalance(new BigDecimal(input.get("currentBalance").toString()));
        }
        if (input.get("isActive") != null) builder.isActive((Boolean) input.get("isActive"));
        if (input.get("notes") != null) builder.notes((String) input.get("notes"));
        if (input.get("identityId") != null) builder.identityId(UUID.fromString((String) input.get("identityId")));
        if (input.get("platformId") != null) builder.platformId(UUID.fromString((String) input.get("platformId")));

        return builder.build();
    }

    private PromotionDTO mapToPromotionDTO(Map<String, Object> input) {
        PromotionDTO.PromotionDTOBuilder builder = PromotionDTO.builder();

        if (input.get("description") != null) builder.description((String) input.get("description"));
        if (input.get("bonusAmount") != null) {
            builder.bonusAmount(new BigDecimal(input.get("bonusAmount").toString()));
        }
        if (input.get("rolloverTarget") != null) {
            builder.rolloverTarget(new BigDecimal(input.get("rolloverTarget").toString()));
        }
        if (input.get("deadlineDate") != null) {
            builder.deadlineDate(LocalDate.parse((String) input.get("deadlineDate")));
        }
        if (input.get("notes") != null) builder.notes((String) input.get("notes"));
        if (input.get("accountId") != null) builder.accountId(UUID.fromString((String) input.get("accountId")));

        return builder.build();
    }

    private DepositDTO mapToDepositDTO(Map<String, Object> input) {
        DepositDTO.DepositDTOBuilder<?, ?> builder = DepositDTO.builder();

        if (input.get("amount") != null) builder.amount(new BigDecimal(input.get("amount").toString()));
        if (input.get("paymentMethod") != null) builder.paymentMethod((String) input.get("paymentMethod"));
        if (input.get("notes") != null) builder.notes((String) input.get("notes"));
        if (input.get("accountId") != null) builder.accountId(UUID.fromString((String) input.get("accountId")));

        return builder.build();
    }

    private WithdrawalDTO mapToWithdrawalDTO(Map<String, Object> input) {
        WithdrawalDTO.WithdrawalDTOBuilder<?, ?> builder = WithdrawalDTO.builder();

        if (input.get("amount") != null) builder.amount(new BigDecimal(input.get("amount").toString()));
        if (input.get("notes") != null) builder.notes((String) input.get("notes"));
        if (input.get("accountId") != null) builder.accountId(UUID.fromString((String) input.get("accountId")));

        return builder.build();
    }

    private BetOperationDTO mapToBetDTO(Map<String, Object> input) {
        BetOperationDTO.BetOperationDTOBuilder<?, ?> builder = BetOperationDTO.builder();

        if (input.get("amount") != null) builder.amount(new BigDecimal(input.get("amount").toString()));
        if (input.get("eventName") != null) builder.eventName((String) input.get("eventName"));
        if (input.get("odds") != null) builder.odds(new BigDecimal(input.get("odds").toString()));
        if (input.get("notes") != null) builder.notes((String) input.get("notes"));
        if (input.get("accountId") != null) builder.accountId(UUID.fromString((String) input.get("accountId")));

        return builder.build();
    }
}
