package com.betflow.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("DEPOSIT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Deposit extends FinancialOperation {

    @Column(name = "payment_method")
    private String paymentMethod;

    @Override
    public String getOperationType() {
        return "DEPOSIT";
    }
}
