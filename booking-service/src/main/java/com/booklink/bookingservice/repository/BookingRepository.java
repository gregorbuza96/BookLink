package com.booklink.bookingservice.repository;

import com.booklink.bookingservice.model.entity.Booking;
import com.booklink.bookingservice.model.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByUserId(Long userId, Pageable pageable);

    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b
        WHERE b.roomId = :roomId
          AND b.status NOT IN ('CANCELLED')
          AND b.checkInDate < :checkOut
          AND b.checkOutDate > :checkIn
    """)
    boolean existsOverlappingBooking(@Param("roomId") Long roomId,
                                      @Param("checkIn") LocalDate checkIn,
                                      @Param("checkOut") LocalDate checkOut);
}
