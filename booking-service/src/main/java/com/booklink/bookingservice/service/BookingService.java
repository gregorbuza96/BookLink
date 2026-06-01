package com.booklink.bookingservice.service;

import com.booklink.bookingservice.client.HotelClient;
import com.booklink.bookingservice.model.dto.BookingDto;
import com.booklink.bookingservice.model.dto.RoomInfo;
import com.booklink.bookingservice.model.entity.Booking;
import com.booklink.bookingservice.model.enums.BookingStatus;
import com.booklink.bookingservice.repository.BookingRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final HotelClient hotelClient;

    public Page<BookingDto> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(this::toDto);
    }

    public BookingDto getBookingById(Long id) {
        return toDto(findOrThrow(id));
    }

    public Page<BookingDto> getBookingsByUser(Long userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable).map(this::toDto);
    }

    @Transactional
    @CircuitBreaker(name = "hotel-service", fallbackMethod = "createBookingFallback")
    @Retry(name = "hotel-service")
    public BookingDto createBooking(BookingDto req, Long userId) {
        validateDates(req);

        RoomInfo room = hotelClient.getRoomById(req.getRoomId());
        if (room == null)
            throw new RuntimeException("Room not found or hotel-service unavailable");
        if ("OCCUPIED".equals(room.getStatus()))
            throw new IllegalStateException("Room " + room.getRoomNumber() + " is occupied");
        if (bookingRepository.existsOverlappingBooking(req.getRoomId(), req.getCheckInDate(), req.getCheckOutDate()))
            throw new IllegalStateException("Room already booked for selected dates");

        long nights = ChronoUnit.DAYS.between(req.getCheckInDate(), req.getCheckOutDate());
        double totalPrice = room.getPricePerNight() * nights;

        Booking booking = Booking.builder()
                .roomId(req.getRoomId()).userId(userId)
                .checkInDate(req.getCheckInDate()).checkOutDate(req.getCheckOutDate())
                .totalPrice(totalPrice).status(BookingStatus.CONFIRMED)
                .build();

        Booking saved = bookingRepository.save(booking);
        hotelClient.updateRoomStatus(req.getRoomId(), "OCCUPIED");
        log.info("Booking {} created for room {} user {}", saved.getId(), req.getRoomId(), userId);
        return toDto(saved);
    }

    public BookingDto createBookingFallback(BookingDto req, Long userId, Exception e) {
        log.error("Circuit breaker OPEN for hotel-service: {}", e.getMessage());
        throw new RuntimeException("Hotel service is currently unavailable. Please try again later.");
    }

    @Transactional
    public void cancelBooking(Long id, Long userId) {
        Booking booking = findOrThrow(id);
        if (!booking.getUserId().equals(userId))
            throw new IllegalArgumentException("Not authorized to cancel this booking");
        if (booking.getStatus() == BookingStatus.CANCELLED)
            throw new IllegalStateException("Booking is already cancelled");
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        hotelClient.updateRoomStatus(booking.getRoomId(), "AVAILABLE");
        log.info("Booking {} cancelled", id);
    }

    @Transactional
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id))
            throw new RuntimeException("Booking not found: " + id);
        bookingRepository.deleteById(id);
    }

    private void validateDates(BookingDto req) {
        if (!req.getCheckOutDate().isAfter(req.getCheckInDate()))
            throw new IllegalArgumentException("Check-out must be after check-in");
    }

    private Booking findOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + id));
    }

    private BookingDto toDto(Booking b) {
        return BookingDto.builder().id(b.getId()).roomId(b.getRoomId()).userId(b.getUserId())
                .checkInDate(b.getCheckInDate()).checkOutDate(b.getCheckOutDate())
                .totalPrice(b.getTotalPrice()).status(b.getStatus()).build();
    }
}
