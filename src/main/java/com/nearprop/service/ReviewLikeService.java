package com.nearprop.service;

import com.nearprop.dto.ReviewLikeDto;

import java.util.List;

public interface ReviewLikeService {

    ReviewLikeDto likeReview(Long reviewId, Long userId);
    
    void unlikeReview(Long reviewId, Long userId);
    
    boolean isReviewLikedByUser(Long reviewId, Long userId);
    
    int getReviewLikeCount(Long reviewId);
    
    List<ReviewLikeDto> getReviewLikes(Long reviewId);
} 