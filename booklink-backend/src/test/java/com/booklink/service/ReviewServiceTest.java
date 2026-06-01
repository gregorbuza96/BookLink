package com.booklink.service;

import com.booklink.exception.ResourceNotFoundException;
import com.booklink.mapper.ReviewMapper;
import com.booklink.model.dto.ReviewDto;
import com.booklink.model.entity.AppUser;
import com.booklink.model.entity.Review;
import com.booklink.model.entity.Room;
import com.booklink.model.enums.*;
import com.booklink.repository.AppUserRepository;
import com.booklink.repository.ReviewRepository;
import com.booklink.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    private Room room;
    private AppUser user;
    private Review review;
    private ReviewDto reviewDto;

    @BeforeEach
    void setUp() {
        room = Room.builder().id(1L).roomNumber(101)
                .type(RoomType.DOUBLE).comfort(ComfortLevel.SUPERIOR)
                .pricePerNight(100.0).capacity(2).status(RoomStatus.AVAILABLE).build();

        user = AppUser.builder().id(1L).username("john")
                .email("john@test.com").password("pass").role(UserRole.USER).build();

        review = Review.builder().id(1L).room(room).user(user)
                .rating(5).comment("Excellent!").createdAt(LocalDateTime.now()).build();

        reviewDto = ReviewDto.builder().id(1L).roomId(1L).userId(1L)
                .rating(5).comment("Excellent!").build();
    }

    @Test
    void createReview_ShouldCreate_WhenRoomAndUserExist() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.save(any())).thenReturn(review);
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        ReviewDto result = reviewService.createReview(reviewDto);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_ShouldThrow_WhenRoomNotFound() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());
        reviewDto.setRoomId(99L);

        assertThrows(ResourceNotFoundException.class, () -> reviewService.createReview(reviewDto));
    }

    @Test
    void createReview_ShouldThrow_WhenUserNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        reviewDto.setUserId(99L);

        assertThrows(ResourceNotFoundException.class, () -> reviewService.createReview(reviewDto));
    }

    @Test
    void getReviewById_ShouldReturn_WhenFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        ReviewDto result = reviewService.getReviewById(1L);

        assertNotNull(result);
    }

    @Test
    void getReviewById_ShouldThrow_WhenNotFound() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.getReviewById(99L));
    }

    @Test
    void deleteReview_ShouldDelete_WhenExists() {
        when(reviewRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> reviewService.deleteReview(1L));

        verify(reviewRepository).deleteById(1L);
    }

    @Test
    void deleteReview_ShouldThrow_WhenNotFound() {
        when(reviewRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> reviewService.deleteReview(99L));
    }
}
