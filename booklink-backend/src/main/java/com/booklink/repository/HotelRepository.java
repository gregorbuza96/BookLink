package com.booklink.repository;

import com.booklink.model.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    boolean existsByName(String name);

    Page<Hotel> findByCity(String city, Pageable pageable);

    Page<Hotel> findByCountry(String country, Pageable pageable);

    List<Hotel> findByStarRating(Integer starRating);
}
