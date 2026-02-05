package com.betflow.services;

import com.betflow.dto.operation.*;
import com.betflow.dto.statistics.IdentityProfitDTO;
import com.betflow.entities.*;
import com.betflow.enums.BetOutcome;
import com.betflow.enums.WithdrawalStatus;
import com.betflow.exceptions.ResourceNotFoundException;
import com.betflow.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialOperationService {

    private final FinancialOperationRepository financialOperationRepository;
    private final AccountRepository accountRepository;
    private final IdentityRepository identityRepository;

    // ==================== DEPOSIT OPERATIONS ====================

    public List<DepositDTO> getAllDeposits() {
        return financialOperationRepository.findAllDeposits().stream()
                .map(this::mapDepositToDTO)
                .collect(Collectors.toList());
    }

    public List<DepositDTO> getDepositsByAccount(UUID accountId) {
        return financialOperationRepository.findDepositsByAccountId(accountId).stream()
                .map(this::mapDepositToDTO)
                .collect(Collectors.toList());
    }

    public DepositDTO getDepositById(UUID id) {
        FinancialOperation operation = financialOperationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deposit", "id", id));
        if (!(operation instanceof Deposit)) {
            throw new ResourceNotFoundException("Deposit", "id", id);
        }
        return mapDepositToDTO((Deposit) operation);
    }

    @Transactional
    public DepositDTO createDeposit(DepositDTO dto) {
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", dto.getAccountId()));

        Deposit deposit = Deposit.builder()
                .amount(dto.getAmount())
                .notes(dto.getNotes())
                .paymentMethod(dto.getPaymentMethod())
                .account(account)
                .operationDate(LocalDateTime.now())
                .build();

        // Update account balance
        account.setCurrentBalance(account.getCurrentBalance().add(dto.getAmount()));
        accountRepository.save(account);

        Deposit savedDeposit = financialOperationRepository.save(deposit);
        log.info("Deposit created: {} on account {}", dto.getAmount(), account.getUsername());
        return mapDepositToDTO(savedDeposit);
    }

    // ==================== WITHDRAWAL OPERATIONS ====================

    public List<WithdrawalDTO> getAllWithdrawals() {
        return financialOperationRepository.findAllWithdrawals().stream()
                .map(this::mapWithdrawalToDTO)
                .collect(Collectors.toList());
    }

    public List<WithdrawalDTO> getWithdrawalsByAccount(UUID accountId) {
        return financialOperationRepository.findWithdrawalsByAccountId(accountId).stream()
                .map(this::mapWithdrawalToDTO)
                .collect(Collectors.toList());
    }

    public List<WithdrawalDTO> getPendingWithdrawals() {
        return financialOperationRepository.findWithdrawalsByStatus(WithdrawalStatus.REQUESTED).stream()
                .map(this::mapWithdrawalToDTO)
                .collect(Collectors.toList());
    }

    public List<WithdrawalDTO> getWithdrawalsByStatus(String status) {
        WithdrawalStatus withdrawalStatus = WithdrawalStatus.valueOf(status.toUpperCase());
        return financialOperationRepository.findWithdrawalsByStatus(withdrawalStatus).stream()
                .map(this::mapWithdrawalToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public WithdrawalDTO createWithdrawal(WithdrawalDTO dto) {
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", dto.getAccountId()));

        Withdrawal withdrawal = Withdrawal.builder()
                .amount(dto.getAmount())
                .notes(dto.getNotes())
                .status(WithdrawalStatus.REQUESTED)
                .account(account)
                .operationDate(LocalDateTime.now())
                .build();

        // Deduct from account balance
        account.setCurrentBalance(account.getCurrentBalance().subtract(dto.getAmount()));
        accountRepository.save(account);

        Withdrawal savedWithdrawal = financialOperationRepository.save(withdrawal);
        log.info("Withdrawal created: {} from account {}", dto.getAmount(), account.getUsername());
        return mapWithdrawalToDTO(savedWithdrawal);
    }

    @Transactional
    public WithdrawalDTO updateWithdrawalStatus(UUID id, WithdrawalDTO dto) {
        FinancialOperation operation = financialOperationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Withdrawal", "id", id));

        if (!(operation instanceof Withdrawal)) {
            throw new ResourceNotFoundException("Withdrawal", "id", id);
        }

        Withdrawal withdrawal = (Withdrawal) operation;

        if (dto.getStatus() != null) {
            withdrawal.setStatus(dto.getStatus());
            if (dto.getStatus() == WithdrawalStatus.COMPLETED && dto.getArrivalDate() == null) {
                withdrawal.setArrivalDate(LocalDateTime.now());
            }
        }

        if (dto.getArrivalDate() != null) {
            withdrawal.setArrivalDate(dto.getArrivalDate());
        }

        Withdrawal savedWithdrawal = financialOperationRepository.save(withdrawal);
        log.info("Withdrawal status updated: {} -> {}", id, dto.getStatus());
        return mapWithdrawalToDTO(savedWithdrawal);
    }

    // ==================== BET OPERATIONS ====================

    public List<BetOperationDTO> getAllBetOperations() {
        return financialOperationRepository.findAllBetOperations().stream()
                .map(this::mapBetToDTO)
                .collect(Collectors.toList());
    }

    public List<BetOperationDTO> getBetsByAccount(UUID accountId) {
        return financialOperationRepository.findBetOperationsByAccountId(accountId).stream()
                .map(this::mapBetToDTO)
                .collect(Collectors.toList());
    }

    public List<BetOperationDTO> getPendingBets() {
        return financialOperationRepository.findPendingBets().stream()
                .map(this::mapBetToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BetOperationDTO createBetOperation(BetOperationDTO dto) {
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", dto.getAccountId()));

        BetOperation bet = BetOperation.builder()
                .amount(dto.getAmount())
                .notes(dto.getNotes())
                .eventName(dto.getEventName())
                .odds(dto.getOdds())
                .outcome(dto.getOutcome())
                .account(account)
                .operationDate(LocalDateTime.now())
                .build();

        // Deduct stake from balance
        account.setCurrentBalance(account.getCurrentBalance().subtract(dto.getAmount()));
        accountRepository.save(account);

        BetOperation savedBet = financialOperationRepository.save(bet);
        log.info("Bet created: {} @ {} on account {}", dto.getEventName(), dto.getOdds(), account.getUsername());
        return mapBetToDTO(savedBet);
    }

    @Transactional
    public BetOperationDTO updateBetOutcome(UUID id, BetOperationDTO dto) {
        FinancialOperation operation = financialOperationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BetOperation", "id", id));

        if (!(operation instanceof BetOperation)) {
            throw new ResourceNotFoundException("BetOperation", "id", id);
        }

        BetOperation bet = (BetOperation) operation;

        if (dto.getOutcome() != null && bet.getOutcome() == null) {
            bet.setOutcome(dto.getOutcome());

            Account account = bet.getAccount();

            if (dto.getOutcome() == BetOutcome.WIN) {
                // Add winnings (stake * odds)
                BigDecimal winnings = bet.getAmount().multiply(bet.getOdds());
                account.setCurrentBalance(account.getCurrentBalance().add(winnings));
                accountRepository.save(account);
            } else if (dto.getOutcome() == BetOutcome.VOID) {
                // Return stake
                account.setCurrentBalance(account.getCurrentBalance().add(bet.getAmount()));
                accountRepository.save(account);
            }
            // LOSS: stake already deducted, no action needed
        }

        BetOperation savedBet = financialOperationRepository.save(bet);
        log.info("Bet outcome updated: {} -> {}", id, dto.getOutcome());
        return mapBetToDTO(savedBet);
    }

    // ==================== GENERAL OPERATIONS ====================

    public List<FinancialOperationDTO> getAllOperations() {
        return financialOperationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<FinancialOperationDTO> getOperationsByAccount(UUID accountId) {
        return financialOperationRepository.findByAccountIdOrderByDateDesc(accountId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<FinancialOperationDTO> getOperationsByIdentity(UUID identityId) {
        return financialOperationRepository.findByIdentityId(identityId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<FinancialOperationDTO> getOperationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return financialOperationRepository.findByDateRange(startDate, endDate).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteOperation(UUID id) {
        FinancialOperation operation = financialOperationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FinancialOperation", "id", id));

        financialOperationRepository.delete(operation);
        log.info("Financial operation deleted: {}", id);
    }

    // ==================== STATISTICS ====================

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

    // ==================== MAPPERS ====================

    private FinancialOperationDTO mapToDTO(FinancialOperation operation) {
        FinancialOperationDTO.FinancialOperationDTOBuilder<?, ?> builder = FinancialOperationDTO.builder()
                .id(operation.getId())
                .operationDate(operation.getOperationDate())
                .amount(operation.getAmount())
                .notes(operation.getNotes())
                .operationType(operation.getOperationType())
                .accountId(operation.getAccount().getId())
                .accountUsername(operation.getAccount().getUsername())
                .platformName(operation.getAccount().getPlatform().getName());

        return builder.build();
    }

    private DepositDTO mapDepositToDTO(Deposit deposit) {
        return DepositDTO.builder()
                .id(deposit.getId())
                .operationDate(deposit.getOperationDate())
                .amount(deposit.getAmount())
                .notes(deposit.getNotes())
                .operationType("DEPOSIT")
                .accountId(deposit.getAccount().getId())
                .accountUsername(deposit.getAccount().getUsername())
                .platformName(deposit.getAccount().getPlatform().getName())
                .paymentMethod(deposit.getPaymentMethod())
                .build();
    }

    private WithdrawalDTO mapWithdrawalToDTO(Withdrawal withdrawal) {
        return WithdrawalDTO.builder()
                .id(withdrawal.getId())
                .operationDate(withdrawal.getOperationDate())
                .amount(withdrawal.getAmount())
                .notes(withdrawal.getNotes())
                .operationType("WITHDRAWAL")
                .accountId(withdrawal.getAccount().getId())
                .accountUsername(withdrawal.getAccount().getUsername())
                .platformName(withdrawal.getAccount().getPlatform().getName())
                .status(withdrawal.getStatus())
                .arrivalDate(withdrawal.getArrivalDate())
                .build();
    }

    private BetOperationDTO mapBetToDTO(BetOperation bet) {
        return BetOperationDTO.builder()
                .id(bet.getId())
                .operationDate(bet.getOperationDate())
                .amount(bet.getAmount())
                .notes(bet.getNotes())
                .operationType("BET")
                .accountId(bet.getAccount().getId())
                .accountUsername(bet.getAccount().getUsername())
                .platformName(bet.getAccount().getPlatform().getName())
                .eventName(bet.getEventName())
                .odds(bet.getOdds())
                .outcome(bet.getOutcome())
                .build();
    }
}
