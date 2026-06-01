package com.booklink.hotelservice.repository;

import com.booklink.hotelservice.model.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {}
