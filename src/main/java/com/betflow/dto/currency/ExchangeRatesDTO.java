package com.betflow.dto.currency;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRatesDTO {
    private String baseCurrency;
    private String date;

    // Main currencies
    private BigDecimal usd;
    private BigDecimal gbp;
    private BigDecimal eur;
    private BigDecimal chf;
    private BigDecimal jpy;
    private BigDecimal cad;
    private BigDecimal aud;
    private BigDecimal cny;
    private BigDecimal inr;
    private BigDecimal brl;

    // All rates for lookup
    private Map<String, Number> allRates;

    public BigDecimal getRate(String currency) {
        if (allRates == null) return null;
        Number rate = allRates.get(currency.toUpperCase());
        return rate != null ? BigDecimal.valueOf(rate.doubleValue()) : null;
    }
}
