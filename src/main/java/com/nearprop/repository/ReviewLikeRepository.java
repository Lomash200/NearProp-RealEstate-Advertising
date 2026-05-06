package com.nearprop.repository;

import com.nearprop.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    
    Optional<ReviewLike> findByReviewIdAndUserId(Long reviewId, Long userId);
    
    void deleteByReviewIdAndUserId(Long reviewId, Long userId);
    
    int countByReviewId(Long reviewId);
    
    List<ReviewLike> findByReviewId(Long reviewId);
    
    boolean existsByReviewIdAndUserId(Long reviewId, Long userId);
    
    @Query("SELECT COUNT(rl) > 0 FROM ReviewLike rl WHERE rl.review.id = :reviewId AND rl.user.id = :userId")
    boolean isReviewLikedByUser(Long reviewId, Long userId);
} 