package com.betflow.dto.operation;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DepositDTO extends FinancialOperationDTO {

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
}
