package com.betflow.controllers;

import com.betflow.dto.operation.*;
import com.betflow.services.FinancialOperationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operations")
@RequiredArgsConstructor
public class FinancialOperationController {

    private final FinancialOperationService financialOperationService;

    // ==================== GENERAL OPERATIONS ====================

    @GetMapping
    public ResponseEntity<List<FinancialOperationDTO>> getAllOperations() {
        return ResponseEntity.ok(financialOperationService.getAllOperations());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<FinancialOperationDTO>> getOperationsByAccount(@PathVariable UUID accountId) {
        return ResponseEntity.ok(financialOperationService.getOperationsByAccount(accountId));
    }

    @GetMapping("/identity/{identityId}")
    public ResponseEntity<List<FinancialOperationDTO>> getOperationsByIdentity(@PathVariable UUID identityId) {
        return ResponseEntity.ok(financialOperationService.getOperationsByIdentity(identityId));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<FinancialOperationDTO>> getOperationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(financialOperationService.getOperationsByDateRange(startDate, endDate));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOperation(@PathVariable UUID id) {
        financialOperationService.deleteOperation(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== DEPOSITS ====================

    @GetMapping("/deposits")
    public ResponseEntity<List<DepositDTO>> getAllDeposits() {
        return ResponseEntity.ok(financialOperationService.getAllDeposits());
    }

    @GetMapping("/deposits/{id}")
    public ResponseEntity<DepositDTO> getDepositById(@PathVariable UUID id) {
        return ResponseEntity.ok(financialOperationService.getDepositById(id));
    }

    @GetMapping("/deposits/account/{accountId}")
    public ResponseEntity<List<DepositDTO>> getDepositsByAccount(@PathVariable UUID accountId) {
        return ResponseEntity.ok(financialOperationService.getDepositsByAccount(accountId));
    }

    @PostMapping("/deposits")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DepositDTO> createDeposit(@RequestBody @Valid DepositDTO dto) {
        DepositDTO created = financialOperationService.createDeposit(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ==================== WITHDRAWALS ====================

    @GetMapping("/withdrawals")
    public ResponseEntity<List<WithdrawalDTO>> getAllWithdrawals() {
        return ResponseEntity.ok(financialOperationService.getAllWithdrawals());
    }

    @GetMapping("/withdrawals/account/{accountId}")
    public ResponseEntity<List<WithdrawalDTO>> getWithdrawalsByAccount(@PathVariable UUID accountId) {
        return ResponseEntity.ok(financialOperationService.getWithdrawalsByAccount(accountId));
    }

    @GetMapping("/withdrawals/pending")
    public ResponseEntity<List<WithdrawalDTO>> getPendingWithdrawals() {
        return ResponseEntity.ok(financialOperationService.getPendingWithdrawals());
    }

    @GetMapping("/withdrawals/status/{status}")
    public ResponseEntity<List<WithdrawalDTO>> getWithdrawalsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(financialOperationService.getWithdrawalsByStatus(status));
    }

    @PostMapping("/withdrawals")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<WithdrawalDTO> createWithdrawal(@RequestBody @Valid WithdrawalDTO dto) {
        WithdrawalDTO created = financialOperationService.createWithdrawal(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/withdrawals/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<WithdrawalDTO> updateWithdrawalStatus(
            @PathVariable UUID id,
            @RequestBody WithdrawalDTO dto) {
        return ResponseEntity.ok(financialOperationService.updateWithdrawalStatus(id, dto));
    }

    // ==================== BET OPERATIONS ====================

    @GetMapping("/bets")
    public ResponseEntity<List<BetOperationDTO>> getAllBetOperations() {
        return ResponseEntity.ok(financialOperationService.getAllBetOperations());
    }

    @GetMapping("/bets/account/{accountId}")
    public ResponseEntity<List<BetOperationDTO>> getBetsByAccount(@PathVariable UUID accountId) {
        return ResponseEntity.ok(financialOperationService.getBetsByAccount(accountId));
    }

    @GetMapping("/bets/pending")
    public ResponseEntity<List<BetOperationDTO>> getPendingBets() {
        return ResponseEntity.ok(financialOperationService.getPendingBets());
    }

    @PostMapping("/bets")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BetOperationDTO> createBetOperation(@RequestBody @Valid BetOperationDTO dto) {
        BetOperationDTO created = financialOperationService.createBetOperation(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/bets/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BetOperationDTO> updateBetOutcome(
            @PathVariable UUID id,
            @RequestBody BetOperationDTO dto) {
        return ResponseEntity.ok(financialOperationService.updateBetOutcome(id, dto));
    }
}
