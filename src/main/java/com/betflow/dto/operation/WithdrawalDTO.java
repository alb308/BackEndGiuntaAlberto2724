package com.betflow.dto.operation;

import com.betflow.enums.WithdrawalStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WithdrawalDTO extends FinancialOperationDTO {
    private WithdrawalStatus status;
    private LocalDateTime arrivalDate;
}
