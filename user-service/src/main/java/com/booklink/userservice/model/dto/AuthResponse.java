package com.booklink.userservice.model.dto;

import lombok.*;

@Getter @Setter @AllArgsConstructor @Builder
public class AuthResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String token;
}
