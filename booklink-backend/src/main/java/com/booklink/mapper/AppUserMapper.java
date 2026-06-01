package com.booklink.mapper;

import com.booklink.model.dto.AppUserDto;
import com.booklink.model.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    AppUser toEntity(AppUserDto dto);

    @Mapping(target = "password", ignore = true)
    AppUserDto toDto(AppUser user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntity(AppUserDto dto, @MappingTarget AppUser user);
}
