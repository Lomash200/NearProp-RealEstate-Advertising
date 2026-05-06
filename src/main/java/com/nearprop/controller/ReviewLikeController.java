package com.nearprop.controller;

import com.nearprop.dto.ReviewLikeDto;
import com.nearprop.entity.User;
import com.nearprop.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    @PostMapping("/{reviewId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewLikeDto> likeReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User currentUser) {
        ReviewLikeDto likeDto = reviewLikeService.likeReview(reviewId, currentUser.getId());
        return new ResponseEntity<>(likeDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{reviewId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unlikeReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User currentUser) {
        reviewLikeService.unlikeReview(reviewId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{reviewId}/liked")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Boolean>> isReviewLiked(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User currentUser) {
        boolean isLiked = reviewLikeService.isReviewLikedByUser(reviewId, currentUser.getId());
        Map<String, Boolean> response = new HashMap<>();
        response.put("liked", isLiked);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reviewId}/likes/count")
    public ResponseEntity<Map<String, Integer>> getLikeCount(@PathVariable Long reviewId) {
        int count = reviewLikeService.getReviewLikeCount(reviewId);
        Map<String, Integer> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reviewId}/likes")
    public ResponseEntity<List<ReviewLikeDto>> getReviewLikes(@PathVariable Long reviewId) {
        List<ReviewLikeDto> likes = reviewLikeService.getReviewLikes(reviewId);
        return ResponseEntity.ok(likes);
    }
} 