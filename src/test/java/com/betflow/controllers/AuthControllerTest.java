package com.betflow.controllers;

import com.betflow.dto.auth.LoginRequestDTO;
import com.betflow.dto.auth.LoginResponseDTO;
import com.betflow.dto.user.UserDTO;
import com.betflow.enums.UserRole;
import com.betflow.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for simple controller testing
public class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthService authService;

        @Autowired
        private ObjectMapper objectMapper;

        private UserDTO userDTO;
        private LoginRequestDTO loginRequestDTO;
        private LoginResponseDTO loginResponseDTO;

        @BeforeEach
        void setUp() {
                userDTO = UserDTO.builder()
                                .id(UUID.randomUUID())
                                .username("testuser")
                                .email("test@example.com")
                                .password("Password123!") // Password should be valid according to constraints if any
                                .name("Test")
                                .surname("User")
                                .role(UserRole.OBSERVER)
                                .build();

                loginRequestDTO = LoginRequestDTO.builder()
                                .email("test@example.com")
                                .password("Password123!")
                                .build();

                loginResponseDTO = LoginResponseDTO.builder()
                                .accessToken("mocked-jwt-token")
                                .tokenType("Bearer")
                                .user(userDTO)
                                .build();
        }

        @Test
        void register_Success() throws Exception {
                when(authService.register(any(UserDTO.class), eq(null))).thenReturn(userDTO);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.email").value("test@example.com"))
                                .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        void login_Success() throws Exception {
                when(authService.login(any(LoginRequestDTO.class))).thenReturn(loginResponseDTO);

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").value("mocked-jwt-token"))
                                .andExpect(jsonPath("$.tokenType").value("Bearer"));
        }
}
