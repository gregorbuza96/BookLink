package com.booklink.mapper;

import com.booklink.model.dto.HotelDto;
import com.booklink.model.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    @Mapping(target = "rooms", ignore = true)
    Hotel toEntity(HotelDto dto);

    HotelDto toDto(Hotel hotel);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    void updateEntity(HotelDto dto, @MappingTarget Hotel hotel);
}
