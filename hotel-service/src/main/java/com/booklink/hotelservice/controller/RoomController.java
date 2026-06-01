package com.booklink.hotelservice.controller;

import com.booklink.hotelservice.model.dto.RoomDto;
import com.booklink.hotelservice.service.RoomService;
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
    public Page<RoomDto> getAll(@PageableDefault(size = 10, sort = "roomNumber") Pageable pageable) {
        return roomService.getAllRooms(pageable);
    }

    @GetMapping("/{id}")
    public RoomDto getById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    @GetMapping("/hotel/{hotelId}")
    public Page<RoomDto> getByHotel(@PathVariable Long hotelId,
                                     @PageableDefault(size = 10) Pageable pageable) {
        return roomService.getRoomsByHotel(hotelId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomDto create(@Valid @RequestBody RoomDto dto) {
        return roomService.createRoom(dto);
    }

    @PutMapping("/{id}")
    public RoomDto update(@PathVariable Long id, @Valid @RequestBody RoomDto dto) {
        return roomService.updateRoom(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        roomService.deleteRoom(id);
    }
}
