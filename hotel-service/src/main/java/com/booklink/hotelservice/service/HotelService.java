package com.booklink.hotelservice.service;

import com.booklink.hotelservice.cache.HotelCacheService;
import com.booklink.hotelservice.model.dto.HotelDto;
import com.booklink.hotelservice.model.entity.Hotel;
import com.booklink.hotelservice.repository.HotelRepository;
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
    private final HotelCacheService cacheService;

    public Page<HotelDto> getAllHotels(Pageable pageable) {
        return hotelRepository.findAll(pageable).map(this::toDto);
    }

    public HotelDto getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found: " + id));
        return cacheService.getCachedHotel(id, toDto(hotel));
    }

    @Transactional
    public HotelDto createHotel(HotelDto dto) {
        Hotel hotel = toEntity(dto);
        HotelDto saved = toDto(hotelRepository.save(hotel));
        cacheService.evictAllHotels();
        return saved;
    }

    @Transactional
    public HotelDto updateHotel(Long id, HotelDto dto) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found: " + id));
        hotel.setName(dto.getName()); hotel.setAddress(dto.getAddress());
        hotel.setCity(dto.getCity()); hotel.setCountry(dto.getCountry());
        hotel.setStarRating(dto.getStarRating()); hotel.setPhone(dto.getPhone());
        hotel.setEmail(dto.getEmail()); hotel.setDescription(dto.getDescription());
        HotelDto saved = toDto(hotelRepository.save(hotel));
        cacheService.evictHotel(id);
        return saved;
    }

    @Transactional
    public void deleteHotel(Long id) {
        hotelRepository.findById(id).orElseThrow(() -> new RuntimeException("Hotel not found: " + id));
        hotelRepository.deleteById(id);
        cacheService.evictHotel(id);
    }

    private HotelDto toDto(Hotel h) {
        return HotelDto.builder().id(h.getId()).name(h.getName()).address(h.getAddress())
                .city(h.getCity()).country(h.getCountry()).starRating(h.getStarRating())
                .phone(h.getPhone()).email(h.getEmail()).description(h.getDescription()).build();
    }

    private Hotel toEntity(HotelDto dto) {
        return Hotel.builder().name(dto.getName()).address(dto.getAddress())
                .city(dto.getCity()).country(dto.getCountry()).starRating(dto.getStarRating())
                .phone(dto.getPhone()).email(dto.getEmail()).description(dto.getDescription()).build();
    }
}
