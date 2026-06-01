package com.booklink.service;

import com.booklink.exception.ResourceNotFoundException;
import com.booklink.mapper.AmenityMapper;
import com.booklink.model.dto.AmenityDto;
import com.booklink.model.entity.Amenity;
import com.booklink.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmenityService {

    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    public List<AmenityDto> getAllAmenities() {
        log.info("Fetching all amenities");
        return amenityRepository.findAll().stream()
                .map(amenityMapper::toDto)
                .toList();
    }

    public AmenityDto getAmenityById(Long id) {
        return amenityRepository.findById(id)
                .map(amenityMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity not found with id: " + id));
    }

    @Transactional
    public AmenityDto createAmenity(AmenityDto request) {
        log.info("Creating amenity: {}", request.getName());
        if (amenityRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Amenity already exists: " + request.getName());
        }
        Amenity amenity = amenityMapper.toEntity(request);
        return amenityMapper.toDto(amenityRepository.save(amenity));
    }

    @Transactional
    public AmenityDto updateAmenity(Long id, AmenityDto request) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity not found with id: " + id));
        amenityMapper.updateEntity(request, amenity);
        return amenityMapper.toDto(amenityRepository.save(amenity));
    }

    @Transactional
    public void deleteAmenity(Long id) {
        log.info("Deleting amenity with id: {}", id);
        if (!amenityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Amenity not found with id: " + id);
        }
        amenityRepository.deleteById(id);
    }
}
