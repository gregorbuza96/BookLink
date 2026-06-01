package com.booklink.service;

import com.booklink.exception.ResourceNotFoundException;
import com.booklink.mapper.ReviewMapper;
import com.booklink.model.dto.ReviewDto;
import com.booklink.model.entity.AppUser;
import com.booklink.model.entity.Review;
import com.booklink.model.entity.Room;
import com.booklink.repository.AppUserRepository;
import com.booklink.repository.ReviewRepository;
import com.booklink.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RoomRepository roomRepository;
    private final AppUserRepository userRepository;
    private final ReviewMapper reviewMapper;

    public Page<ReviewDto> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable).map(reviewMapper::toDto);
    }

    public Page<ReviewDto> getReviewsByRoom(Long roomId, Pageable pageable) {
        return reviewRepository.findByRoomId(roomId, pageable).map(reviewMapper::toDto);
    }

    public ReviewDto getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(reviewMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
    }

    @Transactional
    public ReviewDto createReview(ReviewDto request) {
        log.info("Creating review for room: {} by user: {}", request.getRoomId(), request.getUserId());
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + request.getRoomId()));
        AppUser user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Review review = Review.builder()
                .room(room)
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Transactional
    public ReviewDto updateReview(Long id, ReviewDto request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        reviewMapper.updateEntity(request, review);
        return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(Long id) {
        log.info("Deleting review with id: {}", id);
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }
}
