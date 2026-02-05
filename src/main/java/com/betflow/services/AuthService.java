package com.betflow.services;

import com.betflow.dto.auth.LoginRequestDTO;
import com.betflow.dto.auth.LoginResponseDTO;
import com.betflow.dto.user.UserDTO;
import com.betflow.entities.User;
import com.betflow.enums.UserRole;
import com.betflow.exceptions.BadRequestException;
import com.betflow.exceptions.DuplicateResourceException;
import com.betflow.exceptions.ResourceNotFoundException;
import com.betflow.repositories.UserRepository;
import com.betflow.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AvatarService avatarService;

    @Transactional
    public UserDTO register(UserDTO dto, MultipartFile avatar) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("User", "email", dto.getEmail());
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("User", "username", dto.getUsername());
        }

        String avatarUrl = null;
        if (avatar != null && !avatar.isEmpty()) {
            avatarUrl = avatarService.uploadAvatar(avatar);
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .surname(dto.getSurname())
                .avatarUrl(avatarUrl)
                .role(dto.getRole() != null ? dto.getRole() : UserRole.OBSERVER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getEmail());
        return mapToDTO(savedUser);
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        String token = jwtService.generateToken(user);
        log.info("User logged in: {}", user.getEmail());

        return LoginResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .user(mapToDTO(user))
                .build();
    }

    public UserDTO getUserDTO(User user) {
        return mapToDTO(user);
    }

    @Transactional
    public UserDTO updateAvatar(UUID userId, MultipartFile avatar) {
        if (avatar == null || avatar.isEmpty()) {
            // Return current user if no avatar file is provided
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            return mapToDTO(user);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getAvatarUrl() != null) {
            avatarService.deleteAvatar(user.getAvatarUrl());
        }

        String newAvatarUrl = avatarService.uploadAvatar(avatar);
        user.setAvatarUrl(newAvatarUrl);

        User savedUser = userRepository.save(user);
        log.info("Avatar updated for user: {}", savedUser.getEmail());
        return mapToDTO(savedUser);
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .surname(user.getSurname())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .registrationDate(user.getRegistrationDate())
                .build();
    }
}
