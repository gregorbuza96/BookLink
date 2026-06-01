package com.booklink.service;

import com.booklink.exception.ResourceNotFoundException;
import com.booklink.mapper.HotelMapper;
import com.booklink.model.dto.HotelDto;
import com.booklink.model.entity.Hotel;
import com.booklink.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelMapper hotelMapper;

    @InjectMocks
    private HotelService hotelService;

    private Hotel hotel;
    private HotelDto hotelDto;

    @BeforeEach
    void setUp() {
        hotel = Hotel.builder()
                .id(1L).name("Grand Hotel").address("Main St 1")
                .city("Bucharest").country("Romania").starRating(5)
                .build();
        hotelDto = HotelDto.builder()
                .id(1L).name("Grand Hotel").address("Main St 1")
                .city("Bucharest").country("Romania").starRating(5)
                .build();
    }

    @Test
    void getAllHotels_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(hotelRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(hotel)));
        when(hotelMapper.toDto(hotel)).thenReturn(hotelDto);

        Page<HotelDto> result = hotelService.getAllHotels(pageable);

        assertEquals(1, result.getTotalElements());
        verify(hotelRepository).findAll(pageable);
    }

    @Test
    void getHotelById_ShouldReturnDto_WhenFound() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelMapper.toDto(hotel)).thenReturn(hotelDto);

        HotelDto result = hotelService.getHotelById(1L);

        assertNotNull(result);
        assertEquals("Grand Hotel", result.getName());
    }

    @Test
    void getHotelById_ShouldThrow_WhenNotFound() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> hotelService.getHotelById(99L));
    }

    @Test
    void createHotel_ShouldSaveAndReturn() {
        when(hotelMapper.toEntity(hotelDto)).thenReturn(hotel);
        when(hotelRepository.save(hotel)).thenReturn(hotel);
        when(hotelMapper.toDto(hotel)).thenReturn(hotelDto);

        HotelDto result = hotelService.createHotel(hotelDto);

        assertNotNull(result);
        verify(hotelRepository).save(hotel);
    }

    @Test
    void updateHotel_ShouldUpdateAndReturn() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(hotel)).thenReturn(hotel);
        when(hotelMapper.toDto(hotel)).thenReturn(hotelDto);

        HotelDto result = hotelService.updateHotel(1L, hotelDto);

        assertNotNull(result);
        verify(hotelMapper).updateEntity(hotelDto, hotel);
    }

    @Test
    void updateHotel_ShouldThrow_WhenNotFound() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> hotelService.updateHotel(99L, hotelDto));
    }

    @Test
    void deleteHotel_ShouldDelete_WhenNoActiveBookings() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        assertDoesNotThrow(() -> hotelService.deleteHotel(1L));

        verify(hotelRepository).delete(hotel);
    }

    @Test
    void deleteHotel_ShouldThrow_WhenNotFound() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> hotelService.deleteHotel(99L));
        verify(hotelRepository, never()).delete(any());
    }
}
