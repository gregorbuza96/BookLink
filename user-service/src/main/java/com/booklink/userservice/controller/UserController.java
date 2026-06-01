package com.booklink.userservice.controller;

import com.booklink.userservice.model.dto.AppUserDto;
import com.booklink.userservice.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AppUserService userService;

    @GetMapping
    public List<AppUserDto> getAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public AppUserDto getById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppUserDto create(@Valid @RequestBody AppUserDto dto) {
        return userService.createUser(dto);
    }

    @PutMapping("/{id}")
    public AppUserDto update(@PathVariable Long id, @Valid @RequestBody AppUserDto dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
