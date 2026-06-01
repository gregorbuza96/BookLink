package com.booklink.hotelservice.cache;

import com.booklink.hotelservice.model.dto.HotelDto;
import com.booklink.hotelservice.model.dto.RoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelCacheService {

    @Cacheable(value = "hotels", key = "#id")
    public HotelDto getCachedHotel(Long id, HotelDto hotel) {
        log.debug("Cache MISS for hotel id={}", id);
        return hotel;
    }

    @CacheEvict(value = "hotels", key = "#id")
    public void evictHotel(Long id) {
        log.debug("Cache EVICT for hotel id={}", id);
    }

    @CacheEvict(value = "hotels", allEntries = true)
    public void evictAllHotels() {
        log.debug("Cache EVICT ALL hotels");
    }

    @Cacheable(value = "rooms", key = "#id")
    public RoomDto getCachedRoom(Long id, RoomDto room) {
        log.debug("Cache MISS for room id={}", id);
        return room;
    }

    @CacheEvict(value = "rooms", key = "#id")
    public void evictRoom(Long id) {}

    @CacheEvict(value = "rooms", allEntries = true)
    public void evictAllRooms() {}
}
