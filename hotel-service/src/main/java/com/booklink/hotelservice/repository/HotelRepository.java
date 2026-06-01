package com.booklink.hotelservice.repository;

import com.booklink.hotelservice.model.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {}
