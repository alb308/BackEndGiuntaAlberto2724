package com.betflow.repositories;

import com.betflow.entities.BetOperation;
import com.betflow.entities.Deposit;
import com.betflow.entities.FinancialOperation;
import com.betflow.entities.Withdrawal;
import com.betflow.enums.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface FinancialOperationRepository extends JpaRepository<FinancialOperation, UUID> {

    List<FinancialOperation> findByAccountId(UUID accountId);

    // ==================== DEPOSIT QUERIES ====================

    @Query("SELECT d FROM Deposit d ORDER BY d.operationDate DESC")
    List<Deposit> findAllDeposits();

    @Query("SELECT d FROM Deposit d WHERE d.account.id = :accountId ORDER BY d.operationDate DESC")
    List<Deposit> findDepositsByAccountId(@Param("accountId") UUID accountId);

    // ==================== WITHDRAWAL QUERIES ====================

    @Query("SELECT w FROM Withdrawal w ORDER BY w.operationDate DESC")
    List<Withdrawal> findAllWithdrawals();

    @Query("SELECT w FROM Withdrawal w WHERE w.account.id = :accountId ORDER BY w.operationDate DESC")
    List<Withdrawal> findWithdrawalsByAccountId(@Param("accountId") UUID accountId);

    @Query("SELECT w FROM Withdrawal w WHERE w.status = :status ORDER BY w.operationDate DESC")
    List<Withdrawal> findWithdrawalsByStatus(@Param("status") WithdrawalStatus status);

    // ==================== BET OPERATION QUERIES ====================

    @Query("SELECT b FROM BetOperation b ORDER BY b.operationDate DESC")
    List<BetOperation> findAllBetOperations();

    @Query("SELECT b FROM BetOperation b WHERE b.account.id = :accountId ORDER BY b.operationDate DESC")
    List<BetOperation> findBetOperationsByAccountId(@Param("accountId") UUID accountId);

    @Query("SELECT b FROM BetOperation b WHERE b.outcome IS NULL ORDER BY b.operationDate DESC")
    List<BetOperation> findPendingBets();

    @Query("SELECT fo FROM FinancialOperation fo WHERE fo.account.id = :accountId ORDER BY fo.operationDate DESC")
    List<FinancialOperation> findByAccountIdOrderByDateDesc(@Param("accountId") UUID accountId);

    @Query("SELECT fo FROM FinancialOperation fo " +
           "WHERE fo.account.identity.id = :identityId " +
           "ORDER BY fo.operationDate DESC")
    List<FinancialOperation> findByIdentityId(@Param("identityId") UUID identityId);

    @Query("SELECT fo FROM FinancialOperation fo " +
           "WHERE fo.operationDate BETWEEN :startDate AND :endDate " +
           "ORDER BY fo.operationDate DESC")
    List<FinancialOperation> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(value = "SELECT COALESCE(SUM(fo.amount), 0) FROM financial_operations fo " +
                   "WHERE fo.account_id IN (SELECT a.id FROM accounts a WHERE a.identity_id = :identityId) " +
                   "AND fo.operation_type = 'DEPOSIT'",
           nativeQuery = true)
    BigDecimal sumDepositsByIdentityId(@Param("identityId") UUID identityId);

    @Query(value = "SELECT COALESCE(SUM(fo.amount), 0) FROM financial_operations fo " +
                   "WHERE fo.account_id IN (SELECT a.id FROM accounts a WHERE a.identity_id = :identityId) " +
                   "AND fo.operation_type = 'WITHDRAWAL'",
           nativeQuery = true)
    BigDecimal sumWithdrawalsByIdentityId(@Param("identityId") UUID identityId);

    @Query(value = "SELECT COALESCE(SUM(fo.amount), 0) FROM financial_operations fo " +
                   "WHERE fo.operation_type = 'DEPOSIT'",
           nativeQuery = true)
    BigDecimal sumAllDeposits();

    @Query(value = "SELECT COALESCE(SUM(fo.amount), 0) FROM financial_operations fo " +
                   "WHERE fo.operation_type = 'WITHDRAWAL'",
           nativeQuery = true)
    BigDecimal sumAllWithdrawals();
}
