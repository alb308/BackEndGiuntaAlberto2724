package com.betflow.services;

import com.betflow.dto.currency.CurrencyConversionDTO;
import com.betflow.dto.currency.ExchangeRatesDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Servizio per la conversione valuta usando ExchangeRate-API
 * Terza API esterna integrata per punti bonus
 */
@Service
@Slf4j
public class CurrencyService {

    @Value("${exchangerate.api.url:https://api.exchangerate-api.com/v4/latest/EUR}")
    private String apiBaseUrl;

    private final RestTemplate restTemplate;

    public CurrencyService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Ottiene i tassi di cambio correnti rispetto all'EUR
     */
    public ExchangeRatesDTO getExchangeRates() {
        return getExchangeRates("EUR");
    }

    /**
     * Ottiene i tassi di cambio per una valuta base specifica
     */
    public ExchangeRatesDTO getExchangeRates(String baseCurrency) {
        try {
            String url = "https://api.exchangerate-api.com/v4/latest/" + baseCurrency.toUpperCase();
            log.info("Fetching exchange rates from: {}", url);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null) {
                @SuppressWarnings("unchecked")
                Map<String, Number> rates = (Map<String, Number>) response.get("rates");

                return ExchangeRatesDTO.builder()
                        .baseCurrency(baseCurrency.toUpperCase())
                        .date((String) response.get("date"))
                        .usd(getBigDecimalRate(rates, "USD"))
                        .gbp(getBigDecimalRate(rates, "GBP"))
                        .eur(getBigDecimalRate(rates, "EUR"))
                        .chf(getBigDecimalRate(rates, "CHF"))
                        .jpy(getBigDecimalRate(rates, "JPY"))
                        .cad(getBigDecimalRate(rates, "CAD"))
                        .aud(getBigDecimalRate(rates, "AUD"))
                        .cny(getBigDecimalRate(rates, "CNY"))
                        .inr(getBigDecimalRate(rates, "INR"))
                        .brl(getBigDecimalRate(rates, "BRL"))
                        .allRates(rates)
                        .build();
            }

            throw new RuntimeException("Empty response from exchange rate API");

        } catch (Exception e) {
            log.error("Failed to fetch exchange rates: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch exchange rates: " + e.getMessage());
        }
    }

    /**
     * Converte un importo da una valuta all'altra
     */
    public CurrencyConversionDTO convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        try {
            log.info("Converting {} {} to {}", amount, fromCurrency, toCurrency);

            // Get rates with the source currency as base
            ExchangeRatesDTO rates = getExchangeRates(fromCurrency.toUpperCase());

            BigDecimal rate = rates.getRate(toCurrency.toUpperCase());
            if (rate == null) {
                throw new IllegalArgumentException("Unknown currency: " + toCurrency);
            }

            BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

            return CurrencyConversionDTO.builder()
                    .originalAmount(amount)
                    .fromCurrency(fromCurrency.toUpperCase())
                    .toCurrency(toCurrency.toUpperCase())
                    .exchangeRate(rate)
                    .convertedAmount(convertedAmount)
                    .date(rates.getDate())
                    .build();

        } catch (Exception e) {
            log.error("Failed to convert currency: {}", e.getMessage());
            throw new RuntimeException("Failed to convert currency: " + e.getMessage());
        }
    }

    /**
     * Converte un importo in EUR in un'altra valuta (utile per la dashboard)
     */
    public BigDecimal convertFromEUR(BigDecimal amountEUR, String targetCurrency) {
        CurrencyConversionDTO conversion = convert(amountEUR, "EUR", targetCurrency);
        return conversion.getConvertedAmount();
    }

    /**
     * Converte un importo da qualsiasi valuta a EUR
     */
    public BigDecimal convertToEUR(BigDecimal amount, String sourceCurrency) {
        CurrencyConversionDTO conversion = convert(amount, sourceCurrency, "EUR");
        return conversion.getConvertedAmount();
    }

    private BigDecimal getBigDecimalRate(Map<String, Number> rates, String currency) {
        Number rate = rates.get(currency);
        if (rate != null) {
            return BigDecimal.valueOf(rate.doubleValue()).setScale(6, RoundingMode.HALF_UP);
        }
        return null;
    }
}
