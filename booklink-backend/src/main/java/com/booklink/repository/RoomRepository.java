package com.booklink.repository;

import com.booklink.model.entity.Room;
import com.booklink.model.enums.RoomStatus;
import com.booklink.model.enums.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(Integer roomNumber);

    Page<Room> findByHotelId(Long hotelId, Pageable pageable);

    List<Room> findByStatus(RoomStatus status);

    List<Room> findByType(RoomType type);

    Page<Room> findByPricePerNightLessThanEqual(Double maxPrice, Pageable pageable);
}
