package com.betflow.services;

import com.betflow.dto.identity.IdentityDTO;
import com.betflow.entities.Identity;
import com.betflow.entities.User;
import com.betflow.exceptions.DuplicateResourceException;
import com.betflow.exceptions.ResourceNotFoundException;
import com.betflow.repositories.AccountRepository;
import com.betflow.repositories.IdentityRepository;
import com.betflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityService {

    private final IdentityRepository identityRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public List<IdentityDTO> getAllIdentities() {
        return identityRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public IdentityDTO getIdentityById(UUID id) {
        return mapToDTO(identityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Identity", "id", id)));
    }

    public List<IdentityDTO> getIdentitiesByManager(UUID managerId) {
        return identityRepository.findByManagerId(managerId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<IdentityDTO> getIdentitiesWithExpiringDocuments(int daysAhead) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(daysAhead);
        return identityRepository.findByDocumentExpiryDateBetween(startDate, endDate).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public IdentityDTO createIdentity(IdentityDTO dto) {
        if (identityRepository.existsByFiscalCode(dto.getFiscalCode())) {
            throw new DuplicateResourceException("Identity", "fiscalCode", dto.getFiscalCode());
        }

        Identity identity = Identity.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .fiscalCode(dto.getFiscalCode().toUpperCase())
                .documentExpiryDate(dto.getDocumentExpiryDate())
                .notes(dto.getNotes())
                .build();

        if (dto.getManagerId() != null) {
            User manager = userRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.getManagerId()));
            identity.setManager(manager);
        }

        Identity savedIdentity = identityRepository.save(identity);
        log.info("Identity created: {} {}", savedIdentity.getFirstName(), savedIdentity.getLastName());
        return mapToDTO(savedIdentity);
    }

    @Transactional
    public IdentityDTO updateIdentity(UUID id, IdentityDTO dto) {
        Identity identity = identityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Identity", "id", id));

        if (dto.getFirstName() != null)
            identity.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null)
            identity.setLastName(dto.getLastName());
        if (dto.getFiscalCode() != null && !dto.getFiscalCode().equalsIgnoreCase(identity.getFiscalCode())) {
            if (identityRepository.existsByFiscalCode(dto.getFiscalCode())) {
                throw new DuplicateResourceException("Identity", "fiscalCode", dto.getFiscalCode());
            }
            identity.setFiscalCode(dto.getFiscalCode().toUpperCase());
        }
        if (dto.getDocumentExpiryDate() != null)
            identity.setDocumentExpiryDate(dto.getDocumentExpiryDate());
        if (dto.getNotes() != null)
            identity.setNotes(dto.getNotes());

        Identity savedIdentity = identityRepository.save(identity);
        log.info("Identity updated: {}", savedIdentity.getId());
        return mapToDTO(savedIdentity);
    }

    @Transactional
    public void deleteIdentity(UUID id) {
        Identity identity = identityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Identity", "id", id));
        identityRepository.delete(identity);
        log.info("Identity deleted: {} {}", identity.getFirstName(), identity.getLastName());
    }

    public boolean hasAccountOnPlatform(UUID identityId, UUID platformId) {
        return accountRepository.existsByIdentityIdAndPlatformId(identityId, platformId);
    }

    private IdentityDTO mapToDTO(Identity identity) {
        return IdentityDTO.builder()
                .id(identity.getId())
                .firstName(identity.getFirstName())
                .lastName(identity.getLastName())
                .fullName(identity.getFullName())
                .fiscalCode(identity.getFiscalCode())
                .documentExpiryDate(identity.getDocumentExpiryDate())
                .notes(identity.getNotes())
                .managerId(identity.getManager() != null ? identity.getManager().getId() : null)
                .managerUsername(identity.getManager() != null ? identity.getManager().getUsername() : null)
                .accountsCount(identity.getAccounts() != null ? identity.getAccounts().size() : 0)
                .build();
    }
}
