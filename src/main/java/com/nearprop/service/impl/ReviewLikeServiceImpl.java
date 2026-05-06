package com.nearprop.service.impl;

import com.nearprop.dto.ReviewLikeDto;
import com.nearprop.dto.UserSummaryDto;
import com.nearprop.entity.Review;
import com.nearprop.entity.ReviewLike;
import com.nearprop.entity.User;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.repository.ReviewLikeRepository;
import com.nearprop.repository.ReviewRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewLikeServiceImpl implements ReviewLikeService {

    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewLikeDto likeReview(Long reviewId, Long userId) {
        // Check if already liked
        boolean alreadyLiked = reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId);
        if (alreadyLiked) {
            throw new IllegalStateException("User has already liked this review");
        }
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
                
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Create like
        ReviewLike like = ReviewLike.builder()
                .review(review)
                .user(user)
                .build();
                
        ReviewLike savedLike = reviewLikeRepository.save(like);
        
        return mapToDto(savedLike);
    }

    @Override
    @Transactional
    public void unlikeReview(Long reviewId, Long userId) {
        ReviewLike like = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Review like not found"));
                
        reviewLikeRepository.delete(like);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isReviewLikedByUser(Long reviewId, Long userId) {
        return reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getReviewLikeCount(Long reviewId) {
        return reviewLikeRepository.countByReviewId(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewLikeDto> getReviewLikes(Long reviewId) {
        List<ReviewLike> likes = reviewLikeRepository.findByReviewId(reviewId);
        return likes.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    private ReviewLikeDto mapToDto(ReviewLike like) {
        return ReviewLikeDto.builder()
                .id(like.getId())
                .reviewId(like.getReview().getId())
                .user(mapToUserSummaryDto(like.getUser()))
                .createdAt(like.getCreatedAt())
                .build();
    }
    
    private UserSummaryDto mapToUserSummaryDto(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getMobileNumber())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .build();
    }
} 