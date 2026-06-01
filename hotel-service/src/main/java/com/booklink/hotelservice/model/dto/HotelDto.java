package com.booklink.hotelservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HotelDto {
    private Long id;
    @NotBlank private String name;
    private String address;
    @NotBlank private String city;
    @NotBlank private String country;
    private Integer starRating;
    private String phone;
    private String email;
    private String description;
}
