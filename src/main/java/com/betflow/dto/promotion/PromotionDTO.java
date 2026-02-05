package com.betflow.dto.promotion;

import com.betflow.enums.PromotionStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionDTO {

    private UUID id;

    @NotBlank(message = "Description is required")
    private String description;

    @DecimalMin(value = "0.00")
    private BigDecimal bonusAmount;

    @DecimalMin(value = "0.00")
    private BigDecimal rolloverTarget;

    private BigDecimal rolloverDone;
    private BigDecimal rolloverPercentage;
    private LocalDate deadlineDate;
    private PromotionStatus status;

    @NotNull(message = "Account ID is required")
    private UUID accountId;
    private String accountUsername;
    private String platformName;
    private String notes;
}
