package com.nearprop.service;

import com.nearprop.dto.CreateReviewDto;
import com.nearprop.dto.ReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewDto createReview(CreateReviewDto reviewDto, Long userId);
    
    ReviewDto updateReview(Long reviewId, CreateReviewDto reviewDto, Long userId);
    
    void deleteReview(Long reviewId, Long userId);
    
    ReviewDto getReview(Long reviewId);
    
    ReviewDto getReview(Long reviewId, Long currentUserId);
    
    Page<ReviewDto> getPropertyReviews(Long propertyId, Pageable pageable);
    
    Page<ReviewDto> getPropertyReviews(Long propertyId, Long currentUserId, Pageable pageable);
    
    Page<ReviewDto> getUserReviews(Long userId, Pageable pageable);
    
    Double getAverageRatingForProperty(Long propertyId);
    
    Long getReviewCountForProperty(Long propertyId);
} 