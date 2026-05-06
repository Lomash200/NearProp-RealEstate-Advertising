package com.nearprop.service;

import com.nearprop.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserFollowingService {
    
    /**
     * Follow a user
     * @param followedId ID of the user to follow
     * @return The followed user's details
     */
    UserDto followUser(Long followedId);
    
    /**
     * Unfollow a user
     * @param followedId ID of the user to unfollow
     * @return The unfollowed user's details
     */
    UserDto unfollowUser(Long followedId);
    
    /**
     * Check if current user follows another user
     * @param followedId ID of the user to check
     * @return True if current user follows the specified user
     */
    boolean isFollowing(Long followedId);
    
    /**
     * Get all users followed by the current user
     * @param pageable Pagination information
     * @return Page of user DTOs
     */
    Page<UserDto> getFollowing(Pageable pageable);
    
    /**
     * Get followers of the current user
     * @param pageable Pagination information
     * @return Page of user DTOs
     */
    Page<UserDto> getFollowers(Pageable pageable);
    
    /**
     * Get all users followed by the specified user
     * @param userId User ID
     * @param pageable Pagination information
     * @return Page of user DTOs
     */
    Page<UserDto> getUserFollowing(Long userId, Pageable pageable);
    
    /**
     * Get followers of the specified user
     * @param userId User ID
     * @param pageable Pagination information
     * @return Page of user DTOs
     */
    Page<UserDto> getUserFollowers(Long userId, Pageable pageable);
    
    /**
     * Get follower count for a user
     * @param userId User ID
     * @return Number of followers
     */
    long getFollowerCount(Long userId);
    
    /**
     * Get following count for a user
     * @param userId User ID
     * @return Number of users being followed
     */
    long getFollowingCount(Long userId);
} 