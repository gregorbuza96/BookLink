package com.booklink.hotelservice.model.dto;

import com.booklink.hotelservice.model.enums.ComfortLevel;
import com.booklink.hotelservice.model.enums.RoomStatus;
import com.booklink.hotelservice.model.enums.RoomType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoomDto {
    private Long id;
    @NotNull private Integer roomNumber;
    @NotNull private RoomType type;
    @NotNull private ComfortLevel comfort;
    @NotNull private Double pricePerNight;
    @NotNull private Integer capacity;
    private RoomStatus status;
    private String description;
    private Long hotelId;
    private String hotelName;
    private List<Long> amenityIds;
}
