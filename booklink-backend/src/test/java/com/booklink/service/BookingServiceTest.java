package com.booklink.service;

import com.booklink.exception.BookingConflictException;
import com.booklink.exception.ResourceNotFoundException;
import com.booklink.mapper.BookingMapper;
import com.booklink.model.dto.BookingDto;
import com.booklink.model.entity.AppUser;
import com.booklink.model.entity.Booking;
import com.booklink.model.entity.Room;
import com.booklink.model.enums.BookingStatus;
import com.booklink.model.enums.ComfortLevel;
import com.booklink.model.enums.RoomStatus;
import com.booklink.model.enums.RoomType;
import com.booklink.model.enums.UserRole;
import com.booklink.repository.AppUserRepository;
import com.booklink.repository.BookingRepository;
import com.booklink.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    private Room room;
    private AppUser user;
    private BookingDto bookingDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        room = Room.builder()
                .id(1L).roomNumber(101)
                .type(RoomType.DOUBLE).comfort(ComfortLevel.SUPERIOR)
                .pricePerNight(150.0).capacity(2)
                .status(RoomStatus.AVAILABLE)
                .build();

        user = AppUser.builder()
                .id(1L).username("john").email("john@test.com")
                .password("pass").role(UserRole.USER)
                .build();

        bookingDto = BookingDto.builder()
                .roomId(1L).userId(1L)
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(3))
                .status(BookingStatus.CONFIRMED)
                .build();

        booking = Booking.builder()
                .id(1L).room(room).user(user)
                .checkInDate(bookingDto.getCheckInDate())
                .checkOutDate(bookingDto.getCheckOutDate())
                .totalPrice(300.0)
                .status(BookingStatus.CONFIRMED)
                .build();
    }

    @Test
    void createBooking_ShouldCreate_WhenRoomAvailable() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.existsOverlappingBooking(any(), any(), any())).thenReturn(false);
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.createBooking(bookingDto);

        assertNotNull(result);
        verify(bookingRepository).save(any(Booking.class));
        verify(roomRepository).save(room);
    }

    @Test
    void createBooking_ShouldThrow_WhenRoomOccupied() {
        room.setStatus(RoomStatus.OCCUPIED);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        assertThrows(BookingConflictException.class, () -> bookingService.createBooking(bookingDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_ShouldThrow_WhenOverlappingBookingExists() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.existsOverlappingBooking(any(), any(), any())).thenReturn(true);

        assertThrows(BookingConflictException.class, () -> bookingService.createBooking(bookingDto));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_ShouldThrow_WhenRoomNotFound() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());
        bookingDto.setRoomId(99L);

        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(bookingDto));
    }

    @Test
    void createBooking_ShouldThrow_WhenInvalidDates() {
        bookingDto.setCheckInDate(LocalDate.now().plusDays(3));
        bookingDto.setCheckOutDate(LocalDate.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(bookingDto));
        verify(roomRepository, never()).findById(any());
    }

    @Test
    void cancelBooking_ShouldCancel_WhenConfirmed() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertDoesNotThrow(() -> bookingService.cancelBooking(1L));

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(RoomStatus.AVAILABLE, room.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancelBooking_ShouldThrow_WhenAlreadyCancelled() {
        booking.setStatus(BookingStatus.CANCELLED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(IllegalStateException.class, () -> bookingService.cancelBooking(1L));
    }

    @Test
    void cancelBooking_ShouldThrow_WhenNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.cancelBooking(99L));
    }

    @Test
    void getBookingById_ShouldThrow_WhenNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingById(99L));
    }

    @Test
    void deleteBooking_ShouldDelete_WhenExists() {
        when(bookingRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> bookingService.deleteBooking(1L));

        verify(bookingRepository).deleteById(1L);
    }

    @Test
    void deleteBooking_ShouldThrow_WhenNotFound() {
        when(bookingRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> bookingService.deleteBooking(99L));
    }
}
