package com.booklink.service;

import com.booklink.exception.ResourceNotFoundException;
import com.booklink.mapper.UserProfileMapper;
import com.booklink.model.dto.UserProfileDto;
import com.booklink.model.entity.AppUser;
import com.booklink.model.entity.UserProfile;
import com.booklink.repository.AppUserRepository;
import com.booklink.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository profileRepository;
    private final AppUserRepository userRepository;
    private final UserProfileMapper profileMapper;

    public UserProfileDto getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .map(profileMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user id: " + userId));
    }

    public UserProfileDto getProfileById(Long id) {
        return profileRepository.findById(id)
                .map(profileMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));
    }

    @Transactional
    public UserProfileDto createOrUpdateProfile(Long userId, UserProfileDto request) {
        log.info("Creating/updating profile for user: {}", userId);
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        UserProfile profile = profileRepository.findByUserId(userId).orElseGet(UserProfile::new);
        profileMapper.updateEntity(request, profile);
        profile.setUser(user);
        return profileMapper.toDto(profileRepository.save(profile));
    }

    @Transactional
    public void deleteProfile(Long userId) {
        log.info("Deleting profile for user: {}", userId);
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user id: " + userId));
        profileRepository.delete(profile);
    }
}
