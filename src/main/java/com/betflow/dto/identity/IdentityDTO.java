package com.betflow.dto.identity;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentityDTO {

    private UUID id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String fullName;

    @NotBlank(message = "Fiscal code is required")
    private String fiscalCode;

    @FutureOrPresent(message = "Document expiry date must be in the present or future")
    private LocalDate documentExpiryDate;

    private String email;
    private String phone;
    private String notes;
    private UUID managerId;
    private String managerUsername;
    private int accountsCount;
}
