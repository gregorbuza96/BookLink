package com.booklink.controller;

import com.booklink.model.dto.UserProfileDto;
import com.booklink.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService profileService;

    @GetMapping
    public UserProfileDto getProfile(@PathVariable Long userId) {
        return profileService.getProfileByUserId(userId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserProfileDto createOrUpdateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserProfileDto request) {
        return profileService.createOrUpdateProfile(userId, request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(@PathVariable Long userId) {
        profileService.deleteProfile(userId);
    }
}
