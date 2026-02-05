package com.betflow.services;

import com.betflow.dto.user.UserDTO;
import com.betflow.dto.user.UserUpdateDTO;
import com.betflow.entities.User;
import com.betflow.enums.UserRole;
import com.betflow.exceptions.DuplicateResourceException;
import com.betflow.exceptions.ResourceNotFoundException;
import com.betflow.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AvatarService avatarService;

    @InjectMocks
    private UserService userService;

    private User sampleUser;
    private UserUpdateDTO updateDTO;
    private final UUID USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(USER_ID)
                .username("testuser")
                .email("test@example.com")
                .password("encodedBadPassword")
                .name("Test")
                .surname("User")
                .role(UserRole.OBSERVER)
                .build();

        updateDTO = UserUpdateDTO.builder()
                .username("newUsername")
                .email("new@example.com")
                .name("NewName")
                .build();
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(sampleUser));

        UserDTO result = userService.getUserById(USER_ID);

        assertNotNull(result);
        assertEquals(sampleUser.getEmail(), result.getEmail());
        verify(userRepository).findById(USER_ID);
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(USER_ID));
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByUsername("newUsername")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO result = userService.updateUser(USER_ID, updateDTO);

        assertEquals("newUsername", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("NewName", result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_DuplicateEmail() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByUsername("newUsername")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.updateUser(USER_ID, updateDTO));
        verify(userRepository, never()).save(any(User.class));
    }
}
