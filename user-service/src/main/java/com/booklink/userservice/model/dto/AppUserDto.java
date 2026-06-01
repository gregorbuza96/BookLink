package com.booklink.userservice.model.dto;

import com.booklink.userservice.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppUserDto {
    private Long id;

    @NotBlank
    private String username;

    @NotBlank @Email
    private String email;

    private String password;

    @NotNull
    private UserRole role;
}
