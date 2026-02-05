package com.betflow.controllers;

import com.betflow.dto.statistics.DashboardDTO;
import com.betflow.dto.statistics.IdentityProfitDTO;
import com.betflow.services.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * GET /api/statistics/dashboard
     * Ottiene le statistiche aggregate per la dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard() {
        log.info("REST request to get dashboard statistics");
        DashboardDTO dashboard = statisticsService.getDashboardStatistics();
        return ResponseEntity.ok(dashboard);
    }

    /**
     * GET /api/statistics/profits/{identityId}
     * Calcola il profitto per una specifica identità
     */
    @GetMapping("/profits/{identityId}")
    public ResponseEntity<IdentityProfitDTO> getIdentityProfit(@PathVariable UUID identityId) {
        log.info("REST request to get profit for identity: {}", identityId);
        IdentityProfitDTO profit = statisticsService.calculateProfitByIdentity(identityId);
        return ResponseEntity.ok(profit);
    }

    /**
     * GET /api/statistics/profits
     * Ottiene le statistiche di profitto per tutte le identità
     */
    @GetMapping("/profits")
    public ResponseEntity<List<IdentityProfitDTO>> getAllProfits() {
        log.info("REST request to get all identities profits");
        List<IdentityProfitDTO> profits = statisticsService.getAllIdentitiesProfits();
        return ResponseEntity.ok(profits);
    }

    /**
     * GET /api/statistics/profits/profitable
     * Ottiene solo le identità con profitto positivo
     */
    @GetMapping("/profits/profitable")
    public ResponseEntity<List<IdentityProfitDTO>> getProfitableIdentities() {
        log.info("REST request to get profitable identities");
        List<IdentityProfitDTO> profits = statisticsService.getProfitableIdentities();
        return ResponseEntity.ok(profits);
    }

    /**
     * GET /api/statistics/profits/unprofitable
     * Ottiene solo le identità in perdita
     */
    @GetMapping("/profits/unprofitable")
    public ResponseEntity<List<IdentityProfitDTO>> getUnprofitableIdentities() {
        log.info("REST request to get unprofitable identities");
        List<IdentityProfitDTO> profits = statisticsService.getUnprofitableIdentities();
        return ResponseEntity.ok(profits);
    }
}
