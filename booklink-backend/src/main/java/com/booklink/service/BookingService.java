package com.booklink.service;

import com.booklink.exception.BookingConflictException;
import com.booklink.exception.ResourceNotFoundException;
import com.booklink.mapper.BookingMapper;
import com.booklink.model.dto.BookingDto;
import com.booklink.model.entity.AppUser;
import com.booklink.model.entity.Booking;
import com.booklink.model.entity.Room;
import com.booklink.model.enums.BookingStatus;
import com.booklink.model.enums.RoomStatus;
import com.booklink.repository.AppUserRepository;
import com.booklink.repository.BookingRepository;
import com.booklink.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final AppUserRepository userRepository;
    private final BookingMapper bookingMapper;

    public Page<BookingDto> getAllBookings(Pageable pageable) {
        log.info("Fetching all bookings, page: {}", pageable.getPageNumber());
        return bookingRepository.findAll(pageable).map(bookingMapper::toDto);
    }

    public BookingDto getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(bookingMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
    }

    public Page<BookingDto> getBookingsByUser(Long userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable).map(bookingMapper::toDto);
    }

    @Transactional
    public BookingDto createBooking(BookingDto request) {
        log.info("Creating booking for room: {} user: {}", request.getRoomId(), request.getUserId());
        validateDates(request.getCheckInDate(), request.getCheckOutDate());

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + request.getRoomId()));

        if (room.getStatus() == RoomStatus.OCCUPIED) {
            throw new BookingConflictException("Room " + room.getRoomNumber() + " is occupied");
        }

        boolean overlap = bookingRepository.existsOverlappingBooking(
                request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate());
        if (overlap) {
            throw new BookingConflictException("Room " + room.getRoomNumber() + " is already booked for the selected dates");
        }

        AppUser user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        double totalPrice = room.getPricePerNight() * nights;

        Booking booking = Booking.builder()
                .room(room)
                .user(user)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalPrice(totalPrice)
                .status(BookingStatus.CONFIRMED)
                .build();

        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        Booking saved = bookingRepository.save(booking);
        log.info("Booking created with id: {}", saved.getId());
        return bookingMapper.toDto(saved);
    }

    @Transactional
    public BookingDto updateBooking(Long id, BookingDto request) {
        log.info("Updating booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        validateDates(request.getCheckInDate(), request.getCheckOutDate());
        bookingMapper.updateEntity(request, booking);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Transactional
    public void cancelBooking(Long id) {
        log.info("Cancelling booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        Room room = booking.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
        bookingRepository.save(booking);
    }

    @Transactional
    public void deleteBooking(Long id) {
        log.info("Deleting booking with id: {}", id);
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Booking not found with id: " + id);
        }
        bookingRepository.deleteById(id);
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }
}
