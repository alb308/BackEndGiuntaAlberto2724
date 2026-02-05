package com.betflow.dto.operation;

import com.betflow.enums.BetOutcome;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BetOperationDTO extends FinancialOperationDTO {

    @NotBlank(message = "Event name is required")
    private String eventName;

    @NotNull(message = "Odds are required")
    @DecimalMin(value = "1.01", message = "Odds must be greater than 1")
    private BigDecimal odds;

    private BetOutcome outcome;
}
