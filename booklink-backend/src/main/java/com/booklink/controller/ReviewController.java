package com.booklink.controller;

import com.booklink.model.dto.ReviewDto;
import com.booklink.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public Page<ReviewDto> getAllReviews(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return reviewService.getAllReviews(pageable);
    }

    @GetMapping("/{id}")
    public ReviewDto getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping("/room/{roomId}")
    public Page<ReviewDto> getReviewsByRoom(
            @PathVariable Long roomId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return reviewService.getReviewsByRoom(roomId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto createReview(@Valid @RequestBody ReviewDto request) {
        return reviewService.createReview(request);
    }

    @PutMapping("/{id}")
    public ReviewDto updateReview(@PathVariable Long id, @Valid @RequestBody ReviewDto request) {
        return reviewService.updateReview(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }
}
