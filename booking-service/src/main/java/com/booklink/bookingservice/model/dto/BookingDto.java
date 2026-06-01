package com.booklink.bookingservice.model.dto;

import com.booklink.bookingservice.model.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingDto {
    private Long id;
    @NotNull private Long roomId;
    private Long userId;
    @NotNull private LocalDate checkInDate;
    @NotNull private LocalDate checkOutDate;
    private Double totalPrice;
    private BookingStatus status;
    private Integer roomNumber;
    private Double pricePerNight;
}
