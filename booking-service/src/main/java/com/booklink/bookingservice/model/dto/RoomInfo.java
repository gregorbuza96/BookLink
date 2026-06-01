package com.booklink.bookingservice.model.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RoomInfo {
    private Long id;
    private Integer roomNumber;
    private Double pricePerNight;
    private String status;
}
