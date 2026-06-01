package com.booklink.controller;

import com.booklink.model.dto.AmenityDto;
import com.booklink.service.AmenityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;

    @GetMapping
    public List<AmenityDto> getAllAmenities() {
        return amenityService.getAllAmenities();
    }

    @GetMapping("/{id}")
    public AmenityDto getAmenityById(@PathVariable Long id) {
        return amenityService.getAmenityById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AmenityDto createAmenity(@Valid @RequestBody AmenityDto request) {
        return amenityService.createAmenity(request);
    }

    @PutMapping("/{id}")
    public AmenityDto updateAmenity(@PathVariable Long id, @Valid @RequestBody AmenityDto request) {
        return amenityService.updateAmenity(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAmenity(@PathVariable Long id) {
        amenityService.deleteAmenity(id);
    }
}
