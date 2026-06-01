package com.booklink.repository;

import com.booklink.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByRoomId(Long roomId, Pageable pageable);

    List<Review> findByUserId(Long userId);

    boolean existsByRoomIdAndUserId(Long roomId, Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.room.id = :roomId")
    Double findAverageRatingByRoomId(@Param("roomId") Long roomId);
}
