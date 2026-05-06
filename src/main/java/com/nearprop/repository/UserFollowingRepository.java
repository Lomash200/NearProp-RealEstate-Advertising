package com.nearprop.repository;

import com.nearprop.entity.UserFollowing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFollowingRepository extends JpaRepository<UserFollowing, Long> {
    
    /**
     * Check if a user follows another user
     * @param followerId The follower user ID
     * @param followedId The followed user ID
     * @return True if the follow relationship exists
     */
    boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);
    
    /**
     * Delete a follow relationship
     * @param followerId The follower user ID
     * @param followedId The followed user ID
     */
    void deleteByFollowerIdAndFollowedId(Long followerId, Long followedId);
    
    /**
     * Find all users followed by a user
     * @param followerId The follower user ID
     * @param pageable Pagination information
     * @return Page of UserFollowing entities
     */
    Page<UserFollowing> findByFollowerId(Long followerId, Pageable pageable);
    
    /**
     * Find all followers of a user
     * @param followedId The followed user ID
     * @param pageable Pagination information
     * @return Page of UserFollowing entities
     */
    Page<UserFollowing> findByFollowedId(Long followedId, Pageable pageable);
    
    /**
     * Count the number of followers a user has
     * @param followedId The followed user ID
     * @return The number of followers
     */
    long countByFollowedId(Long followedId);
    
    /**
     * Count the number of users a user follows
     * @param followerId The follower user ID
     * @return The number of followed users
     */
    long countByFollowerId(Long followerId);
} 