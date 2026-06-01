package com.booklink.bookingservice.model.entity;

import com.booklink.bookingservice.model.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "booking")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private Long roomId;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private LocalDate checkInDate;
    @Column(nullable = false) private LocalDate checkOutDate;
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
}
