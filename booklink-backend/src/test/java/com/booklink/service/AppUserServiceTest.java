package com.booklink.service;

import com.booklink.exception.ResourceNotFoundException;
import com.booklink.mapper.AppUserMapper;
import com.booklink.model.dto.AppUserDto;
import com.booklink.model.entity.AppUser;
import com.booklink.model.enums.UserRole;
import com.booklink.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock private AppUserRepository userRepository;
    @Mock private AppUserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserService userService;

    private AppUser user;
    private AppUserDto userDto;

    @BeforeEach
    void setUp() {
        user = AppUser.builder()
                .id(1L).username("john").email("john@test.com")
                .password("hashed_secret").role(UserRole.USER)
                .build();
        userDto = AppUserDto.builder()
                .id(1L).username("john").email("john@test.com")
                .password("secret").role(UserRole.USER)
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<AppUserDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("john", result.get(0).getUsername());
    }

    @Test
    void getUserById_ShouldReturnDto_WhenFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        AppUserDto result = userService.getUserById(1L);

        assertEquals("john", result.getUsername());
    }

    @Test
    void getUserById_ShouldThrow_WhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void createUser_ShouldHashPassword_AndSave() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode("secret")).thenReturn("hashed_secret");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        AppUserDto result = userService.createUser(userDto);

        assertNotNull(result);
        verify(passwordEncoder).encode("secret");
        verify(userRepository).save(user);
    }

    @Test
    void createUser_ShouldThrow_WhenUsernameTaken() {
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_ShouldThrow_WhenEmailTaken() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldEncodePassword_WhenNewPasswordProvided() {
        AppUserDto updateDto = AppUserDto.builder()
                .username("john").email("john@test.com")
                .password("newpass").role(UserRole.USER)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("hashed_newpass");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        AppUserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        verify(passwordEncoder).encode("newpass");
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_ShouldNotEncodePassword_WhenPasswordBlank() {
        AppUserDto updateDto = AppUserDto.builder()
                .username("john").email("john@test.com")
                .password("").role(UserRole.USER)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        userService.updateUser(1L, updateDto);

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void deleteUser_ShouldDelete_WhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrow_WhenNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(99L));
    }
}
