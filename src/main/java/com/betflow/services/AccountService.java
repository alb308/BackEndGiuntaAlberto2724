package com.betflow.services;

import com.betflow.dto.account.AccountDTO;
import com.betflow.entities.Account;
import com.betflow.entities.Identity;
import com.betflow.entities.Platform;
import com.betflow.exceptions.BadRequestException;
import com.betflow.exceptions.DuplicateResourceException;
import com.betflow.exceptions.ResourceNotFoundException;
import com.betflow.repositories.AccountRepository;
import com.betflow.repositories.IdentityRepository;
import com.betflow.repositories.PlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final IdentityRepository identityRepository;
    private final PlatformRepository platformRepository;

    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AccountDTO getAccountById(UUID id) {
        Account account = accountRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
        return mapToDTO(account);
    }

    public List<AccountDTO> getAccountsByIdentity(UUID identityId) {
        return accountRepository.findByIdentityId(identityId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AccountDTO> getAccountsByPlatform(UUID platformId) {
        return accountRepository.findByPlatformId(platformId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AccountDTO> getActiveAccounts() {
        return accountRepository.findByIsActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AccountDTO> getLimitedAccounts() {
        return accountRepository.findByIsLimitedTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountDTO createAccount(AccountDTO dto) {
        log.info("Attempting to create account for IdentityID: {} and PlatformID: {}", dto.getIdentityId(),
                dto.getPlatformId());
        if (accountRepository.existsByIdentityIdAndPlatformId(dto.getIdentityId(), dto.getPlatformId())) {
            throw new DuplicateResourceException("Account already exists for this Identity on this Platform");
        }

        Identity identity = identityRepository.findById(dto.getIdentityId())
                .orElseThrow(() -> new ResourceNotFoundException("Identity not found with ID: " + dto.getIdentityId()));

        Platform platform = platformRepository.findById(dto.getPlatformId())
                .orElseThrow(() -> new ResourceNotFoundException("Platform not found with ID: " + dto.getPlatformId()));

        Account account = Account.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .currentBalance(dto.getCurrentBalance() != null ? dto.getCurrentBalance() : BigDecimal.ZERO)
                .identity(identity)
                .platform(platform)
                .build();

        Account savedAccount = accountRepository.save(account);
        log.info("Account created: {} on {}", savedAccount.getUsername(), platform.getName());
        return mapToDTO(savedAccount);
    }

    @Transactional
    public AccountDTO updateAccount(UUID id, AccountDTO dto) {
        log.debug("Updating account {} with data: {}", id, dto);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));

        if (dto.getUsername() != null) {
            account.setUsername(dto.getUsername());
        }

        if (dto.getPassword() != null) {
            account.setPassword(dto.getPassword());
        }

        if (dto.getCurrentBalance() != null) {
            account.setCurrentBalance(dto.getCurrentBalance());
        }

        if (dto.getIsActive() != null) {
            account.setIsActive(dto.getIsActive());
        }

        if (dto.getIsLimited() != null) {
            account.setIsLimited(dto.getIsLimited());
        }

        Account savedAccount = accountRepository.save(account);
        log.info("Account updated: {}", savedAccount.getId());
        return mapToDTO(savedAccount);
    }

    @Transactional
    public AccountDTO updateBalance(UUID id, BigDecimal newBalance) {
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Balance cannot be negative");
        }

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));

        account.setCurrentBalance(newBalance);
        Account savedAccount = accountRepository.save(account);
        log.info("Account balance updated: {} -> {}", savedAccount.getId(), newBalance);
        return mapToDTO(savedAccount);
    }

    @Transactional
    public void deleteAccount(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));

        accountRepository.delete(account);
        log.info("Account deleted: {}", account.getUsername());
    }

    public BigDecimal getTotalBalanceByIdentity(UUID identityId) {
        BigDecimal total = accountRepository.sumCurrentBalanceByIdentityId(identityId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public boolean existsByIdentityAndPlatform(UUID identityId, UUID platformId) {
        return accountRepository.existsByIdentityIdAndPlatformId(identityId, platformId);
    }

    private AccountDTO mapToDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .username(account.getUsername())
                .currentBalance(account.getCurrentBalance())
                .isActive(account.getIsActive())
                .isLimited(account.getIsLimited())
                .identityId(account.getIdentity().getId())
                .identityFullName(account.getIdentity().getFullName())
                .platformId(account.getPlatform().getId())
                .platformName(account.getPlatform().getName())
                .promotionsCount(account.getPromotions() != null ? account.getPromotions().size() : 0)
                .operationsCount(account.getFinancialOperations() != null ? account.getFinancialOperations().size() : 0)
                .build();
    }
}
