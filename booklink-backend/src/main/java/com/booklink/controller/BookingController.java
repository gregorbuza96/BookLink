package com.booklink.controller;

import com.booklink.model.dto.BookingDto;
import com.booklink.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public Page<BookingDto> getAllBookings(
            @PageableDefault(size = 10, sort = "checkInDate") Pageable pageable) {
        return bookingService.getAllBookings(pageable);
    }

    @GetMapping("/{id}")
    public BookingDto getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @GetMapping("/user/{userId}")
    public Page<BookingDto> getBookingsByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "checkInDate") Pageable pageable) {
        return bookingService.getBookingsByUser(userId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@Valid @RequestBody BookingDto request) {
        return bookingService.createBooking(request);
    }

    @PutMapping("/{id}")
    public BookingDto updateBooking(@PathVariable Long id, @Valid @RequestBody BookingDto request) {
        return bookingService.updateBooking(id, request);
    }

    @PatchMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }
}
