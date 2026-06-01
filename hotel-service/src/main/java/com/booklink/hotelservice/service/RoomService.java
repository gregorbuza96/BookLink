package com.booklink.hotelservice.service;

import com.booklink.hotelservice.model.dto.RoomDto;
import com.booklink.hotelservice.model.entity.Amenity;
import com.booklink.hotelservice.model.entity.Hotel;
import com.booklink.hotelservice.model.entity.Room;
import com.booklink.hotelservice.model.enums.RoomStatus;
import com.booklink.hotelservice.repository.AmenityRepository;
import com.booklink.hotelservice.repository.HotelRepository;
import com.booklink.hotelservice.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;

    public Page<RoomDto> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable).map(this::toDto);
    }

    public RoomDto getRoomById(Long id) {
        return toDto(findOrThrow(id));
    }

    public Page<RoomDto> getRoomsByHotel(Long hotelId, Pageable pageable) {
        return roomRepository.findByHotelId(hotelId, pageable).map(this::toDto);
    }

    @Transactional
    public RoomDto createRoom(RoomDto dto) {
        if (roomRepository.findByRoomNumber(dto.getRoomNumber()).isPresent())
            throw new IllegalArgumentException("Room number already exists: " + dto.getRoomNumber());

        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found: " + dto.getHotelId()));

        List<Amenity> amenities = dto.getAmenityIds() != null
                ? amenityRepository.findAllById(dto.getAmenityIds()) : List.of();

        Room room = Room.builder()
                .roomNumber(dto.getRoomNumber()).type(dto.getType()).comfort(dto.getComfort())
                .pricePerNight(dto.getPricePerNight()).capacity(dto.getCapacity())
                .status(dto.getStatus() != null ? dto.getStatus() : RoomStatus.AVAILABLE)
                .description(dto.getDescription()).hotel(hotel).amenities(amenities)
                .build();
        return toDto(roomRepository.save(room));
    }

    @Transactional
    public RoomDto updateRoom(Long id, RoomDto dto) {
        Room room = findOrThrow(id);
        room.setType(dto.getType()); room.setComfort(dto.getComfort());
        room.setPricePerNight(dto.getPricePerNight()); room.setCapacity(dto.getCapacity());
        room.setStatus(dto.getStatus()); room.setDescription(dto.getDescription());
        if (dto.getAmenityIds() != null)
            room.setAmenities(amenityRepository.findAllById(dto.getAmenityIds()));
        return toDto(roomRepository.save(room));
    }

    @Transactional
    public void updateRoomStatus(Long id, RoomStatus status) {
        Room room = findOrThrow(id);
        room.setStatus(status);
        roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = findOrThrow(id);
        if (room.getStatus() == RoomStatus.OCCUPIED)
            throw new IllegalStateException("Cannot delete an occupied room");
        roomRepository.delete(room);
    }

    private Room findOrThrow(Long id) {
        return roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found: " + id));
    }

    private RoomDto toDto(Room r) {
        return RoomDto.builder().id(r.getId()).roomNumber(r.getRoomNumber())
                .type(r.getType()).comfort(r.getComfort()).pricePerNight(r.getPricePerNight())
                .capacity(r.getCapacity()).status(r.getStatus()).description(r.getDescription())
                .hotelId(r.getHotel() != null ? r.getHotel().getId() : null)
                .hotelName(r.getHotel() != null ? r.getHotel().getName() : null)
                .amenityIds(r.getAmenities() != null
                        ? r.getAmenities().stream().map(a -> a.getId()).toList() : List.of())
                .build();
    }
}
