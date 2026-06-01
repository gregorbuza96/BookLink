package com.booklink.hotelservice.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewDto {
    private Long id;
    @NotNull private Long roomId;
    private Long userId;
    private String username;
    @NotNull @Min(1) @Max(5) private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
