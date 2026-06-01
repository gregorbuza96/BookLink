package com.booklink.mapper;

import com.booklink.model.dto.AmenityDto;
import com.booklink.model.entity.Amenity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AmenityMapper {

    @Mapping(target = "rooms", ignore = true)
    Amenity toEntity(AmenityDto dto);

    AmenityDto toDto(Amenity amenity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    void updateEntity(AmenityDto dto, @MappingTarget Amenity amenity);
}
