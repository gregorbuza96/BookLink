package com.booklink.bookingservice.client;

import com.booklink.bookingservice.model.dto.RoomInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HotelClientFallback implements HotelClient {

    @Override
    public RoomInfo getRoomById(Long id) {
        log.warn("hotel-service unavailable — fallback for room {}", id);
        return null;
    }

    @Override
    public void updateRoomStatus(Long id, String status) {
        log.warn("hotel-service unavailable — could not update room {} status to {}", id, status);
    }
}
