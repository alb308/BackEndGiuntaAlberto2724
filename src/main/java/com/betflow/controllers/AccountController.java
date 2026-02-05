package com.betflow.controllers;

import com.betflow.dto.account.AccountDTO;
import com.betflow.services.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping("/identity/{identityId}")
    public ResponseEntity<List<AccountDTO>> getAccountsByIdentity(@PathVariable UUID identityId) {
        return ResponseEntity.ok(accountService.getAccountsByIdentity(identityId));
    }

    @GetMapping("/platform/{platformId}")
    public ResponseEntity<List<AccountDTO>> getAccountsByPlatform(@PathVariable UUID platformId) {
        return ResponseEntity.ok(accountService.getAccountsByPlatform(platformId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<AccountDTO>> getActiveAccounts() {
        return ResponseEntity.ok(accountService.getActiveAccounts());
    }

    @GetMapping("/limited")
    public ResponseEntity<List<AccountDTO>> getLimitedAccounts() {
        return ResponseEntity.ok(accountService.getLimitedAccounts());
    }

    @GetMapping("/check-duplicate")
    public ResponseEntity<Boolean> checkDuplicateAccount(
            @RequestParam UUID identityId,
            @RequestParam UUID platformId) {
        return ResponseEntity.ok(accountService.existsByIdentityAndPlatform(identityId, platformId));
    }

    @GetMapping("/identity/{identityId}/total-balance")
    public ResponseEntity<BigDecimal> getTotalBalanceByIdentity(@PathVariable UUID identityId) {
        return ResponseEntity.ok(accountService.getTotalBalanceByIdentity(identityId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<AccountDTO> createAccount(@RequestBody @Valid AccountDTO dto) {
        AccountDTO created = accountService.createAccount(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<AccountDTO> updateAccount(
            @PathVariable UUID id,
            @RequestBody @Valid AccountDTO dto) {
        return ResponseEntity.ok(accountService.updateAccount(id, dto));
    }

    @PatchMapping("/{id}/balance")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<AccountDTO> updateBalance(
            @PathVariable UUID id,
            @RequestParam BigDecimal newBalance) {
        return ResponseEntity.ok(accountService.updateBalance(id, newBalance));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
