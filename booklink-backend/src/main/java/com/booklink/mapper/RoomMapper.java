package com.booklink.mapper;

import com.booklink.model.dto.RoomDto;
import com.booklink.model.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {AmenityMapper.class})
public interface RoomMapper {

    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    Room toEntity(RoomDto dto);

    @Mapping(target = "hotelId", source = "hotel.id")
    @Mapping(target = "hotelName", source = "hotel.name")
    @Mapping(target = "amenityIds", ignore = true)
    RoomDto toDto(Room room);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    void updateEntity(RoomDto dto, @MappingTarget Room room);
}
