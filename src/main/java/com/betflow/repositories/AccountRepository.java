package com.betflow.repositories;

import com.betflow.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    List<Account> findByIdentityId(UUID identityId);

    List<Account> findByPlatformId(UUID platformId);

    List<Account> findByIsActiveTrue();

    List<Account> findByIsLimitedTrue();

    @Query("SELECT a FROM Account a WHERE a.identity.id = :identityId AND a.platform.id = :platformId")
    Optional<Account> findByIdentityIdAndPlatformId(
            @Param("identityId") UUID identityId,
            @Param("platformId") UUID platformId
    );

    boolean existsByIdentityIdAndPlatformId(UUID identityId, UUID platformId);

    @Query("SELECT SUM(a.currentBalance) FROM Account a WHERE a.identity.id = :identityId")
    BigDecimal sumCurrentBalanceByIdentityId(@Param("identityId") UUID identityId);

    @Query("SELECT SUM(a.currentBalance) FROM Account a")
    BigDecimal sumAllCurrentBalances();

    @Query("SELECT COUNT(a) FROM Account a WHERE a.isActive = true")
    long countActiveAccounts();

    @Query("SELECT COUNT(a) FROM Account a WHERE a.isLimited = true")
    long countLimitedAccounts();

    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.identity " +
           "JOIN FETCH a.platform " +
           "WHERE a.id = :id")
    Optional<Account> findByIdWithDetails(@Param("id") UUID id);
}
