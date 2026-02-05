package com.betflow.repositories;

import com.betflow.entities.Identity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IdentityRepository extends JpaRepository<Identity, UUID> {

        Optional<Identity> findByFiscalCode(String fiscalCode);

        boolean existsByFiscalCode(String fiscalCode);

        List<Identity> findByManagerId(UUID managerId);

        @Query("SELECT i FROM Identity i WHERE i.documentExpiryDate BETWEEN :startDate AND :endDate")
        List<Identity> findByDocumentExpiryDateBetween(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i FROM Identity i WHERE i.documentExpiryDate <= :date")
        List<Identity> findByDocumentExpiringBefore(@Param("date") LocalDate date);

        @Query("SELECT i FROM Identity i LEFT JOIN FETCH i.accounts WHERE i.id = :id")
        Optional<Identity> findByIdWithAccounts(@Param("id") UUID id);
}
