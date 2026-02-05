package com.betflow.entities;

import com.betflow.enums.WithdrawalStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("WITHDRAWAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Withdrawal extends FinancialOperation {

    @Enumerated(EnumType.STRING)
    @Column(name = "withdrawal_status")
    @Builder.Default
    private WithdrawalStatus status = WithdrawalStatus.REQUESTED;

    @Column(name = "arrival_date")
    private LocalDateTime arrivalDate;

    @Override
    public String getOperationType() {
        return "WITHDRAWAL";
    }
}
