package com.betflow.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {

    private UUID id;

    @NotBlank(message = "Username is required")
    private String username;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    private BigDecimal currentBalance;

    private String notes;

    private Boolean isActive;
    private Boolean isLimited;

    @NotNull(message = "Identity ID is required")
    private UUID identityId;
    private String identityFullName;

    @NotNull(message = "Platform ID is required")
    private UUID platformId;
    private String platformName;

    private int promotionsCount;
    private int operationsCount;
}
