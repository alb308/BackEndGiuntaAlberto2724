package com.betflow.controllers;

import com.betflow.dto.currency.CurrencyConversionDTO;
import com.betflow.dto.currency.ExchangeRatesDTO;
import com.betflow.services.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Controller per la conversione valuta
 * Utilizza ExchangeRate-API (terza API esterna)
 */
@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
@Slf4j
public class CurrencyController {

    private final CurrencyService currencyService;

    /**
     * GET /api/currency/rates
     * Ottiene i tassi di cambio correnti rispetto all'EUR
     */
    @GetMapping("/rates")
    public ResponseEntity<ExchangeRatesDTO> getExchangeRates() {
        log.info("REST request to get exchange rates for EUR");
        ExchangeRatesDTO rates = currencyService.getExchangeRates();
        return ResponseEntity.ok(rates);
    }

    /**
     * GET /api/currency/rates/{baseCurrency}
     * Ottiene i tassi di cambio per una valuta base specifica
     */
    @GetMapping("/rates/{baseCurrency}")
    public ResponseEntity<ExchangeRatesDTO> getExchangeRates(@PathVariable String baseCurrency) {
        log.info("REST request to get exchange rates for {}", baseCurrency);
        ExchangeRatesDTO rates = currencyService.getExchangeRates(baseCurrency);
        return ResponseEntity.ok(rates);
    }

    /**
     * GET /api/currency/convert
     * Converte un importo da una valuta all'altra
     *
     * @param amount L'importo da convertire
     * @param from La valuta di origine (es. "EUR")
     * @param to La valuta di destinazione (es. "USD")
     */
    @GetMapping("/convert")
    public ResponseEntity<CurrencyConversionDTO> convert(
            @RequestParam BigDecimal amount,
            @RequestParam String from,
            @RequestParam String to
    ) {
        log.info("REST request to convert {} {} to {}", amount, from, to);
        CurrencyConversionDTO conversion = currencyService.convert(amount, from, to);
        return ResponseEntity.ok(conversion);
    }

    /**
     * GET /api/currency/convert/eur-to/{currency}
     * Converte un importo da EUR a un'altra valuta
     */
    @GetMapping("/convert/eur-to/{currency}")
    public ResponseEntity<CurrencyConversionDTO> convertFromEUR(
            @RequestParam BigDecimal amount,
            @PathVariable String currency
    ) {
        log.info("REST request to convert {} EUR to {}", amount, currency);
        CurrencyConversionDTO conversion = currencyService.convert(amount, "EUR", currency);
        return ResponseEntity.ok(conversion);
    }

    /**
     * GET /api/currency/convert/to-eur/{currency}
     * Converte un importo da qualsiasi valuta a EUR
     */
    @GetMapping("/convert/to-eur/{currency}")
    public ResponseEntity<CurrencyConversionDTO> convertToEUR(
            @RequestParam BigDecimal amount,
            @PathVariable String currency
    ) {
        log.info("REST request to convert {} {} to EUR", amount, currency);
        CurrencyConversionDTO conversion = currencyService.convert(amount, currency, "EUR");
        return ResponseEntity.ok(conversion);
    }
}
