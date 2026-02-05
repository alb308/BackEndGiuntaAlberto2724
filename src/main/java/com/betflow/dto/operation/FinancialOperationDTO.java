package com.betflow.dto.operation;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FinancialOperationDTO {

    private UUID id;
    private LocalDateTime operationDate;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    private String notes;
    private String operationType;

    @NotNull(message = "Account ID is required")
    private UUID accountId;
    private String accountUsername;
    private String platformName;
}
