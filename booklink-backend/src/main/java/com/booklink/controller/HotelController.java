package com.booklink.controller;

import com.booklink.model.dto.HotelDto;
import com.booklink.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    public Page<HotelDto> getAllHotels(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return hotelService.getAllHotels(pageable);
    }

    @GetMapping("/{id}")
    public HotelDto getHotelById(@PathVariable Long id) {
        return hotelService.getHotelById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HotelDto createHotel(@Valid @RequestBody HotelDto request) {
        return hotelService.createHotel(request);
    }

    @PutMapping("/{id}")
    public HotelDto updateHotel(@PathVariable Long id, @Valid @RequestBody HotelDto request) {
        return hotelService.updateHotel(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
    }
}
