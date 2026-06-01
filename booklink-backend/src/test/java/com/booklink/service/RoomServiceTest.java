package com.booklink.service;

import com.booklink.exception.ResourceNotFoundException;
import com.booklink.exception.RoomAlreadyExistsException;
import com.booklink.exception.RoomOccupiedException;
import com.booklink.exception.ResourceNotFoundException;
import com.booklink.mapper.RoomMapper;
import com.booklink.model.dto.RoomDto;
import com.booklink.model.entity.Room;
import com.booklink.model.enums.ComfortLevel;
import com.booklink.model.enums.RoomStatus;
import com.booklink.model.enums.RoomType;
import com.booklink.repository.AmenityRepository;
import com.booklink.repository.HotelRepository;
import com.booklink.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock private RoomRepository roomRepository;
    @Mock private HotelRepository hotelRepository;
    @Mock private AmenityRepository amenityRepository;
    @Mock private RoomMapper roomMapper;

    @InjectMocks
    private RoomService roomService;

    private RoomDto roomDto;
    private Room room;

    @BeforeEach
    void setUp() {
        roomDto = RoomDto.builder()
                .id(1L).roomNumber(101)
                .type(RoomType.DOUBLE).comfort(ComfortLevel.SUPERIOR)
                .pricePerNight(150.0).capacity(2)
                .status(RoomStatus.AVAILABLE).description("Sea view room")
                .build();

        room = Room.builder()
                .id(1L).roomNumber(101)
                .type(RoomType.DOUBLE).comfort(ComfortLevel.SUPERIOR)
                .pricePerNight(150.0).capacity(2)
                .status(RoomStatus.AVAILABLE).description("Sea view room")
                .build();
    }

    @Test
    void getAllRooms_ShouldReturnPage() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(roomRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(room)));
        when(roomMapper.toDto(room)).thenReturn(roomDto);

        Page<RoomDto> result = roomService.getAllRooms(pageable);

        assertEquals(1, result.getTotalElements());
        verify(roomRepository).findAll(pageable);
    }

    @Test
    void getRoomById_ShouldReturnDto_WhenFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomMapper.toDto(room)).thenReturn(roomDto);

        RoomDto result = roomService.getRoomById(1L);

        assertNotNull(result);
        assertEquals(101, result.getRoomNumber());
    }

    @Test
    void getRoomById_ShouldThrow_WhenNotFound() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roomService.getRoomById(99L));
    }

    @Test
    void addRoom_ShouldSaveRoom_WhenRoomDoesNotExist() {
        when(roomRepository.findByRoomNumber(101)).thenReturn(Optional.empty());
        when(roomMapper.toEntity(roomDto)).thenReturn(room);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toDto(room)).thenReturn(roomDto);

        RoomDto saved = roomService.addRoom(roomDto);

        assertNotNull(saved);
        assertEquals(101, saved.getRoomNumber());
        verify(roomRepository).save(room);
    }

    @Test
    void addRoom_ShouldThrow_WhenRoomAlreadyExists() {
        when(roomRepository.findByRoomNumber(101)).thenReturn(Optional.of(room));

        assertThrows(RoomAlreadyExistsException.class, () -> roomService.addRoom(roomDto));
        verify(roomRepository, never()).save(any());
    }

    @Test
    void deleteRoom_ShouldDelete_WhenAvailable() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        assertDoesNotThrow(() -> roomService.deleteRoom(1L));

        verify(roomRepository).delete(room);
    }

    @Test
    void deleteRoom_ShouldThrow_WhenOccupied() {
        room.setStatus(RoomStatus.OCCUPIED);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        assertThrows(RoomOccupiedException.class, () -> roomService.deleteRoom(1L));
        verify(roomRepository, never()).delete(any());
    }

    @Test
    void deleteRoom_ShouldThrow_WhenNotFound() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roomService.deleteRoom(99L));
        verify(roomRepository, never()).delete(any());
    }
}
