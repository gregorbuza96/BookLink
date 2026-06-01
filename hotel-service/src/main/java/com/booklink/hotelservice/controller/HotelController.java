package com.booklink.hotelservice.controller;

import com.booklink.hotelservice.model.dto.HotelDto;
import com.booklink.hotelservice.service.HotelService;
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
    public Page<HotelDto> getAll(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return hotelService.getAllHotels(pageable);
    }

    @GetMapping("/{id}")
    public HotelDto getById(@PathVariable Long id) {
        return hotelService.getHotelById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HotelDto create(@Valid @RequestBody HotelDto dto) {
        return hotelService.createHotel(dto);
    }

    @PutMapping("/{id}")
    public HotelDto update(@PathVariable Long id, @Valid @RequestBody HotelDto dto) {
        return hotelService.updateHotel(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        hotelService.deleteHotel(id);
    }
}
