package com.booklink.integration;

import com.booklink.model.dto.BookingDto;
import com.booklink.model.dto.HotelDto;
import com.booklink.model.dto.RoomDto;
import com.booklink.model.dto.AppUserDto;
import com.booklink.model.entity.AppUser;
import com.booklink.model.entity.Booking;
import com.booklink.model.entity.Hotel;
import com.booklink.model.entity.Room;
import com.booklink.model.enums.*;
import com.booklink.repository.*;
import com.booklink.service.BookingService;
import com.booklink.service.HotelService;
import com.booklink.service.RoomService;
import com.booklink.service.AppUserService;
import com.booklink.exception.BookingConflictException;
import com.booklink.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HotelBookingIntegrationTest {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private AppUserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // Scenario 1: Complete hotel + room + booking flow
    @Test
    void scenario1_CreateHotelRoomAndBooking_ShouldSucceed() {
        // Create hotel
        HotelDto hotelRequest = HotelDto.builder()
                .name("Test Hotel").address("Test St 1")
                .city("Bucharest").country("Romania").starRating(4)
                .build();
        HotelDto createdHotel = hotelService.createHotel(hotelRequest);
        assertNotNull(createdHotel.getId());

        // Create room under hotel
        RoomDto roomRequest = RoomDto.builder()
                .roomNumber(201).type(RoomType.DOUBLE).comfort(ComfortLevel.SUPERIOR)
                .pricePerNight(200.0).capacity(2).status(RoomStatus.AVAILABLE)
                .hotelId(createdHotel.getId())
                .build();
        RoomDto createdRoom = roomService.addRoom(roomRequest);
        assertNotNull(createdRoom.getId());
        assertEquals(createdHotel.getId(), createdRoom.getHotelId());

        // Create user
        AppUserDto userRequest = AppUserDto.builder()
                .username("testuser1").email("test1@test.com")
                .password("password123").role(UserRole.USER)
                .build();
        AppUserDto createdUser = userService.createUser(userRequest);
        assertNotNull(createdUser.getId());

        // Create booking
        BookingDto bookingRequest = BookingDto.builder()
                .roomId(createdRoom.getId()).userId(createdUser.getId())
                .checkInDate(LocalDate.now().plusDays(5))
                .checkOutDate(LocalDate.now().plusDays(8))
                .build();
        BookingDto createdBooking = bookingService.createBooking(bookingRequest);

        assertNotNull(createdBooking.getId());
        assertEquals(BookingStatus.CONFIRMED, createdBooking.getStatus());
        assertEquals(600.0, createdBooking.getTotalPrice());

        // Verify room is now OCCUPIED
        RoomDto updatedRoom = roomService.getRoomById(createdRoom.getId());
        assertEquals(RoomStatus.OCCUPIED, updatedRoom.getStatus());
    }

    // Scenario 2: Booking conflict — same room, overlapping dates
    @Test
    void scenario2_BookingConflict_ShouldThrowException() {
        // Setup hotel, room, user
        Hotel hotel = hotelRepository.save(Hotel.builder()
                .name("Conflict Hotel").address("Addr 1")
                .city("Cluj").country("Romania").build());

        Room room = roomRepository.save(Room.builder()
                .roomNumber(301).type(RoomType.SINGLE).comfort(ComfortLevel.STANDARD)
                .pricePerNight(100.0).capacity(1).status(RoomStatus.AVAILABLE)
                .hotel(hotel).build());

        AppUser user = userRepository.save(AppUser.builder()
                .username("conflictuser").email("conflict@test.com")
                .password("pass").role(UserRole.USER).build());

        // First booking
        BookingDto firstBooking = BookingDto.builder()
                .roomId(room.getId()).userId(user.getId())
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(5))
                .build();
        bookingService.createBooking(firstBooking);

        // Reset room to AVAILABLE to test overlap detection (not occupied check)
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        // Second booking on overlapping dates — should fail due to overlap query
        BookingDto overlappingBooking = BookingDto.builder()
                .roomId(room.getId()).userId(user.getId())
                .checkInDate(LocalDate.now().plusDays(3))
                .checkOutDate(LocalDate.now().plusDays(7))
                .build();

        assertThrows(BookingConflictException.class,
                () -> bookingService.createBooking(overlappingBooking));
    }

    // Scenario 3: Cancel booking — room returns to AVAILABLE
    @Test
    void scenario3_CancelBooking_ShouldFreeRoom() {
        Hotel hotel = hotelRepository.save(Hotel.builder()
                .name("Cancel Hotel").address("Addr 2")
                .city("Timisoara").country("Romania").build());

        Room room = roomRepository.save(Room.builder()
                .roomNumber(401).type(RoomType.DOUBLE).comfort(ComfortLevel.SUPERIOR)
                .pricePerNight(150.0).capacity(2).status(RoomStatus.AVAILABLE)
                .hotel(hotel).build());

        AppUser user = userRepository.save(AppUser.builder()
                .username("canceluser").email("cancel@test.com")
                .password("pass").role(UserRole.USER).build());

        // Create and confirm booking
        BookingDto bookingRequest = BookingDto.builder()
                .roomId(room.getId()).userId(user.getId())
                .checkInDate(LocalDate.now().plusDays(2))
                .checkOutDate(LocalDate.now().plusDays(4))
                .build();
        BookingDto createdBooking = bookingService.createBooking(bookingRequest);
        assertEquals(BookingStatus.CONFIRMED, createdBooking.getStatus());

        // Cancel it
        bookingService.cancelBooking(createdBooking.getId());

        // Verify booking is CANCELLED
        BookingDto cancelled = bookingService.getBookingById(createdBooking.getId());
        assertEquals(BookingStatus.CANCELLED, cancelled.getStatus());

        // Verify room is AVAILABLE again
        RoomDto roomAfterCancel = roomService.getRoomById(room.getId());
        assertEquals(RoomStatus.AVAILABLE, roomAfterCancel.getStatus());
    }

    // Scenario 4: Get non-existent resource throws ResourceNotFoundException
    @Test
    void scenario4_GetNonExistentHotel_ShouldThrowResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class,
                () -> hotelService.getHotelById(99999L));
    }

    // Scenario 5: Pagination works for bookings
    @Test
    void scenario5_PaginationForBookings_ShouldWork() {
        Hotel hotel = hotelRepository.save(Hotel.builder()
                .name("Page Hotel").address("Addr 3")
                .city("Iasi").country("Romania").build());

        AppUser user = userRepository.save(AppUser.builder()
                .username("pageuser").email("page@test.com")
                .password("pass").role(UserRole.USER).build());

        for (int i = 1; i <= 5; i++) {
            Room room = roomRepository.save(Room.builder()
                    .roomNumber(500 + i).type(RoomType.SINGLE).comfort(ComfortLevel.STANDARD)
                    .pricePerNight(80.0).capacity(1).status(RoomStatus.AVAILABLE)
                    .hotel(hotel).build());
            bookingRepository.save(Booking.builder()
                    .room(room).user(user)
                    .checkInDate(LocalDate.now().plusDays(i))
                    .checkOutDate(LocalDate.now().plusDays(i + 1))
                    .totalPrice(80.0).status(BookingStatus.CONFIRMED).build());
        }

        Page<BookingDto> page = bookingService.getAllBookings(PageRequest.of(0, 3));

        assertEquals(3, page.getSize());
        assertTrue(page.getTotalElements() >= 5);
    }
}
