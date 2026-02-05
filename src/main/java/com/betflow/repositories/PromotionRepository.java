package com.betflow.repositories;

import com.betflow.entities.Promotion;
import com.betflow.enums.PromotionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

    List<Promotion> findByAccountId(UUID accountId);

    List<Promotion> findByStatus(PromotionStatus status);

    @Query("SELECT p FROM Promotion p " +
           "WHERE p.status = :status " +
           "AND p.deadlineDate <= :deadlineDate " +
           "ORDER BY p.deadlineDate ASC")
    List<Promotion> findActivePromotionsExpiringWithinDays(
            @Param("status") PromotionStatus status,
            @Param("deadlineDate") LocalDate deadlineDate
    );

    @Query("SELECT p FROM Promotion p " +
           "WHERE p.status = 'ACTIVE' " +
           "AND p.deadlineDate BETWEEN :startDate AND :endDate " +
           "ORDER BY p.deadlineDate ASC")
    List<Promotion> findActivePromotionsExpiringBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT p FROM Promotion p WHERE p.deadlineDate = :date")
    List<Promotion> findByDeadlineDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(p) FROM Promotion p WHERE p.status = :status")
    long countByStatus(@Param("status") PromotionStatus status);

    @Query("SELECT p FROM Promotion p " +
           "JOIN FETCH p.account a " +
           "JOIN FETCH a.platform " +
           "WHERE p.account.identity.id = :identityId")
    List<Promotion> findByIdentityId(@Param("identityId") UUID identityId);
}
