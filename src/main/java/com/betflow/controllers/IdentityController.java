package com.betflow.controllers;

import com.betflow.dto.identity.IdentityDTO;
import com.betflow.services.IdentityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/identities")
@RequiredArgsConstructor
public class IdentityController {

    private final IdentityService identityService;

    @GetMapping
    public ResponseEntity<List<IdentityDTO>> getAllIdentities() {
        return ResponseEntity.ok(identityService.getAllIdentities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IdentityDTO> getIdentityById(@PathVariable UUID id) {
        return ResponseEntity.ok(identityService.getIdentityById(id));
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<IdentityDTO>> getIdentitiesByManager(@PathVariable UUID managerId) {
        return ResponseEntity.ok(identityService.getIdentitiesByManager(managerId));
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<IdentityDTO>> getIdentitiesWithExpiringDocuments(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(identityService.getIdentitiesWithExpiringDocuments(days));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<IdentityDTO> createIdentity(@RequestBody @Valid IdentityDTO dto) {
        return new ResponseEntity<>(identityService.createIdentity(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<IdentityDTO> updateIdentity(@PathVariable UUID id, @RequestBody IdentityDTO dto) {
        return ResponseEntity.ok(identityService.updateIdentity(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteIdentity(@PathVariable UUID id) {
        identityService.deleteIdentity(id);
        return ResponseEntity.noContent().build();
    }
}
