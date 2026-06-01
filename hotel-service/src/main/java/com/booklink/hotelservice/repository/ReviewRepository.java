package com.booklink.hotelservice.repository;

import com.booklink.hotelservice.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByRoomId(Long roomId, Pageable pageable);
}
