package com.booklink.service;

import com.booklink.exception.ResourceNotFoundException;
import com.booklink.mapper.HotelMapper;
import com.booklink.model.dto.HotelDto;
import com.booklink.model.entity.Hotel;
import com.booklink.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    public Page<HotelDto> getAllHotels(Pageable pageable) {
        log.info("Fetching all hotels, page: {}", pageable.getPageNumber());
        return hotelRepository.findAll(pageable).map(hotelMapper::toDto);
    }

    public HotelDto getHotelById(Long id) {
        log.info("Fetching hotel with id: {}", id);
        return hotelRepository.findById(id)
                .map(hotelMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
    }

    @Transactional
    public HotelDto createHotel(HotelDto request) {
        log.info("Creating hotel: {}", request.getName());
        Hotel hotel = hotelMapper.toEntity(request);
        Hotel saved = hotelRepository.save(hotel);
        log.info("Hotel created with id: {}", saved.getId());
        return hotelMapper.toDto(saved);
    }

    @Transactional
    public HotelDto updateHotel(Long id, HotelDto request) {
        log.info("Updating hotel with id: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        hotelMapper.updateEntity(request, hotel);
        return hotelMapper.toDto(hotelRepository.save(hotel));
    }

    @Transactional
    public void deleteHotel(Long id) {
        log.info("Deleting hotel with id: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        boolean hasActiveBookings = hotel.getRooms().stream()
                .flatMap(room -> room.getBookings().stream())
                .anyMatch(b -> b.getStatus().name().equals("CONFIRMED") || b.getStatus().name().equals("PENDING"));
        if (hasActiveBookings) {
            throw new IllegalStateException("Cannot delete hotel with active bookings");
        }
        hotelRepository.delete(hotel);
        log.info("Hotel deleted: {}", id);
    }
}
