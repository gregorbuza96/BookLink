package com.booklink.mapper;

import com.booklink.model.dto.ReviewDto;
import com.booklink.model.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "room", ignore = true)
    @Mapping(target = "user", ignore = true)
    Review toEntity(ReviewDto dto);

    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomNumber", source = "room.roomNumber")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    ReviewDto toDto(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(ReviewDto dto, @MappingTarget Review review);
}
