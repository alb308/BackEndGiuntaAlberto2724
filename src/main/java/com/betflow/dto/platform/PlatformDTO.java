package com.betflow.dto.platform;

import com.betflow.enums.PlatformType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformDTO {

    private UUID id;

    @NotBlank(message = "Platform name is required")
    private String name;

    @NotBlank(message = "Website URL is required")
    private String websiteUrl;

    @NotNull(message = "Platform type is required")
    private PlatformType type;

    private String notes;
    private int accountsCount;
}
