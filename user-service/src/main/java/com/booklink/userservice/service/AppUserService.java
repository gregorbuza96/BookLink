package com.booklink.userservice.service;

import com.booklink.userservice.exception.ResourceNotFoundException;
import com.booklink.userservice.model.dto.AppUserDto;
import com.booklink.userservice.model.entity.AppUser;
import com.booklink.userservice.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<AppUserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    public AppUserDto getUserById(Long id) {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public AppUserDto createUser(AppUserDto req) {
        log.info("Creating user: {}", req.getUsername());
        if (userRepository.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("Username already taken: " + req.getUsername());
        if (userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already in use: " + req.getEmail());

        AppUser user = AppUser.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .build();
        return toDto(userRepository.save(user));
    }

    @Transactional
    public AppUserDto updateUser(Long id, AppUserDto req) {
        AppUser user = findOrThrow(id);
        if (!user.getUsername().equals(req.getUsername()) && userRepository.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("Username already taken: " + req.getUsername());
        if (!user.getEmail().equals(req.getEmail()) && userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already in use: " + req.getEmail());

        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setRole(req.getRole());
        if (StringUtils.hasText(req.getPassword()))
            user.setPassword(passwordEncoder.encode(req.getPassword()));

        return toDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id))
            throw new ResourceNotFoundException("User not found: " + id);
        userRepository.deleteById(id);
    }

    private AppUser findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private AppUserDto toDto(AppUser u) {
        return AppUserDto.builder()
                .id(u.getId()).username(u.getUsername())
                .email(u.getEmail()).role(u.getRole())
                .build();
    }
}
