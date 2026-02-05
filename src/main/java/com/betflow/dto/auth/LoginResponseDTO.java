package com.betflow.dto.auth;

import com.betflow.dto.user.UserDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private String accessToken;
    private String tokenType;
    private UserDTO user;
}
