package com.nearprop.service.impl;

import com.nearprop.dto.CreateReviewDto;
import com.nearprop.dto.ReviewDto;
import com.nearprop.dto.UserSummaryDto;
import com.nearprop.entity.Property;
import com.nearprop.entity.Review;
import com.nearprop.entity.User;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.exception.UnauthorizedException;
import com.nearprop.repository.PropertyRepository;
import com.nearprop.repository.ReviewLikeRepository;
import com.nearprop.repository.ReviewRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Override
    @Transactional
    public ReviewDto createReview(CreateReviewDto reviewDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                
        Property property = propertyRepository.findById(reviewDto.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + reviewDto.getPropertyId()));
                
        Review review = Review.builder()
                .property(property)
                .user(user)
                .rating(reviewDto.getRating())
                .comment(reviewDto.getComment())
                .build();
                
        Review savedReview = reviewRepository.save(review);
        
        return mapToDto(savedReview, userId);
    }

    @Override
    @Transactional
    public ReviewDto updateReview(Long reviewId, CreateReviewDto reviewDto, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
                
        // Check if the user is the author of the review
        if (!review.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this review");
        }
        
        // Update review fields
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        
        Review updatedReview = reviewRepository.save(review);
        
        return mapToDto(updatedReview, userId);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        // Find the review and eagerly fetch the property and its owner
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
                
        // Explicitly initialize the property owner to avoid LazyInitializationException
        User propertyOwner = userRepository.findById(review.getProperty().getOwner().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Property owner not found"));
                
        // Check if the user is the author of the review or the property owner
        boolean isAuthor = review.getUser().getId().equals(userId);
        boolean isPropertyOwner = propertyOwner.getId().equals(userId);
        
        if (!isAuthor && !isPropertyOwner) {
            throw new UnauthorizedException("You are not authorized to delete this review");
        }
        
        try {
            reviewRepository.delete(review);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete review: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDto getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
                
        return mapToDto(review, null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ReviewDto getReview(Long reviewId, Long currentUserId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
                
        return mapToDto(review, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDto> getPropertyReviews(Long propertyId, Pageable pageable) {
        // Check if property exists
        propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
                
        return reviewRepository.findByPropertyId(propertyId, pageable)
                .map(review -> mapToDto(review, null));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDto> getPropertyReviews(Long propertyId, Long currentUserId, Pageable pageable) {
        // Check if property exists
        propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
                
        return reviewRepository.findByPropertyId(propertyId, pageable)
                .map(review -> mapToDto(review, currentUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDto> getUserReviews(Long userId, Pageable pageable) {
        // Check if user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                
        return reviewRepository.findByUserId(userId, pageable)
                .map(review -> mapToDto(review, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingForProperty(Long propertyId) {
        // Check if property exists
        propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
                
        return reviewRepository.getAverageRatingForProperty(propertyId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getReviewCountForProperty(Long propertyId) {
        // Check if property exists
        propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
                
        return reviewRepository.getReviewCountForProperty(propertyId);
    }
    
    private ReviewDto mapToDto(Review review, Long currentUserId) {
        int likeCount = review.getLikes().size();
        boolean likedByCurrentUser = false;
        
        if (currentUserId != null) {
            likedByCurrentUser = reviewLikeRepository.isReviewLikedByUser(review.getId(), currentUserId);
        }
        
        return ReviewDto.builder()
                .id(review.getId())
                .propertyId(review.getProperty().getId())
                .user(mapToUserSummaryDto(review.getUser()))
                .rating(review.getRating())
                .comment(review.getComment())
                .likeCount(likeCount)
                .likedByCurrentUser(likedByCurrentUser)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
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