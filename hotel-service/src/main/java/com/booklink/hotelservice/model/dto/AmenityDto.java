package com.booklink.hotelservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AmenityDto {
    private Long id;
    @NotBlank private String name;
    private String description;
}
