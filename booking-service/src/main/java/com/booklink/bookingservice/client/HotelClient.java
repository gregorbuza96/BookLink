package com.booklink.bookingservice.client;

import com.booklink.bookingservice.model.dto.RoomInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "hotel-service", url = "${services.hotel-service}",
             fallback = HotelClientFallback.class)
public interface HotelClient {

    @GetMapping("/api/rooms/{id}")
    RoomInfo getRoomById(@PathVariable Long id);

    @PatchMapping("/api/rooms/{id}/status")
    void updateRoomStatus(@PathVariable Long id, @RequestParam String status);
}
