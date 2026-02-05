package com.betflow.controllers;

import com.betflow.dto.auth.LoginRequestDTO;
import com.betflow.dto.auth.LoginResponseDTO;
import com.betflow.dto.user.UserDTO;
import com.betflow.entities.User;
import com.betflow.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid UserDTO userDTO) {
        UserDTO createdUser = authService.register(userDTO, null);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.getUserDTO(user));
    }

    @PatchMapping("/me/avatar")
    public ResponseEntity<UserDTO> updateAvatar(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        return ResponseEntity.ok(authService.updateAvatar(user.getId(), avatar));
    }
}
