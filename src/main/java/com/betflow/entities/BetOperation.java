package com.betflow.entities;

import com.betflow.enums.BetOutcome;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("BET")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BetOperation extends FinancialOperation {

    @Column(name = "event_name")
    private String eventName;

    @Column(precision = 10, scale = 2)
    private BigDecimal odds;

    @Enumerated(EnumType.STRING)
    private BetOutcome outcome;

    @Override
    public String getOperationType() {
        return "BET";
    }
}
