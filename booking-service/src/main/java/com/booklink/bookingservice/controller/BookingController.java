package com.booklink.bookingservice.controller;

import com.booklink.bookingservice.model.dto.BookingDto;
import com.booklink.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public Page<BookingDto> getAll(@PageableDefault(size = 10, sort = "checkInDate") Pageable pageable) {
        return bookingService.getAllBookings(pageable);
    }

    @GetMapping("/{id}")
    public BookingDto getById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @GetMapping("/my")
    public Page<BookingDto> getMyBookings(Principal principal,
                                          @PageableDefault(size = 10) Pageable pageable) {
        Long userId = extractUserId(principal);
        return bookingService.getBookingsByUser(userId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@Valid @RequestBody BookingDto dto, Principal principal) {
        Long userId = extractUserId(principal);
        return bookingService.createBooking(dto, userId);
    }

    @PatchMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id, Principal principal) {
        Long userId = extractUserId(principal);
        bookingService.cancelBooking(id, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }

    private Long extractUserId(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken token) {
            return (Long) token.getDetails();
        }
        throw new IllegalStateException("Cannot extract user ID");
    }
}
