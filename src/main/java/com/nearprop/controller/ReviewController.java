package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.CreateReviewDto;
import com.nearprop.dto.ReviewDto;
import com.nearprop.entity.User;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.exception.UnauthorizedException;
import com.nearprop.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDto> createReview(
            @Valid @RequestBody CreateReviewDto reviewDto,
            @AuthenticationPrincipal User currentUser) {
        // Always create a new review, never update existing one
        ReviewDto createdReview = reviewService.createReview(reviewDto, currentUser.getId());
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable("reviewId") Long reviewId,
            @Valid @RequestBody CreateReviewDto reviewDto,
            @AuthenticationPrincipal User currentUser) {
        ReviewDto updatedReview = reviewService.updateReview(reviewId, reviewDto, currentUser.getId());
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteReview(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal User currentUser) {
        try {
            reviewService.deleteReview(reviewId, currentUser.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Review deleted successfully");
            response.put("reviewId", reviewId);
            
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete review: " + e.getMessage()));
        }
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> getReview(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal User currentUser) {
        ReviewDto review = currentUser != null 
                ? reviewService.getReview(reviewId, currentUser.getId())
                : reviewService.getReview(reviewId);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<Page<ReviewDto>> getPropertyReviews(
            @PathVariable("propertyId") Long propertyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @AuthenticationPrincipal User currentUser) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ReviewDto> reviews = currentUser != null
                ? reviewService.getPropertyReviews(propertyId, currentUser.getId(), pageable)
                : reviewService.getPropertyReviews(propertyId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReviewDto>> getUserReviews(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ReviewDto> reviews = reviewService.getUserReviews(userId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/property/{propertyId}/rating")
    public ResponseEntity<Map<String, Object>> getPropertyRatingStats(
            @PathVariable("propertyId") Long propertyId) {
        Double averageRating = reviewService.getAverageRatingForProperty(propertyId);
        Long reviewCount = reviewService.getReviewCountForProperty(propertyId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("propertyId", propertyId);
        response.put("averageRating", averageRating != null ? averageRating : 0.0);
        response.put("reviewCount", reviewCount);
        
        return ResponseEntity.ok(response);
    }
} 