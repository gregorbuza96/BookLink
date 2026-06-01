package com.booklink.controller;

import com.booklink.model.dto.RoomDto;
import com.booklink.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public Page<RoomDto> getAllRooms(
            @PageableDefault(size = 10, sort = "roomNumber") Pageable pageable) {
        return roomService.getAllRooms(pageable);
    }

    @GetMapping("/{id}")
    public RoomDto getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    @GetMapping("/hotel/{hotelId}")
    public Page<RoomDto> getRoomsByHotel(
            @PathVariable Long hotelId,
            @PageableDefault(size = 10, sort = "roomNumber") Pageable pageable) {
        return roomService.getRoomsByHotel(hotelId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomDto addRoom(@Valid @RequestBody RoomDto request) {
        return roomService.addRoom(request);
    }

    @PutMapping("/{id}")
    public RoomDto updateRoom(@PathVariable Long id, @Valid @RequestBody RoomDto request) {
        return roomService.updateRoom(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
    }
}
