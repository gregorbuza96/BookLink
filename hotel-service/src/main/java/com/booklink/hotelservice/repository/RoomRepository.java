package com.booklink.hotelservice.repository;

import com.booklink.hotelservice.model.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomNumber(Integer roomNumber);
    Page<Room> findByHotelId(Long hotelId, Pageable pageable);
}
