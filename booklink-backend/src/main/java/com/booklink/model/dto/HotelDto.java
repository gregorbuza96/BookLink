package com.booklink.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDto {

    private Long id;

    @NotBlank(message = "Hotel name is required")
    @Size(max = 255, message = "Hotel name cannot exceed 255 characters")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Country is required")
    private String country;

    @Min(value = 1, message = "Star rating must be at least 1")
    @Max(value = 5, message = "Star rating cannot exceed 5")
    private Integer starRating;

    private String phone;

    @Email(message = "Email must be valid")
    private String email;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
}
