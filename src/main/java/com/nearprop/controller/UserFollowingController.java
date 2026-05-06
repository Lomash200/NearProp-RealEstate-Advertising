package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.UserDto;
import com.nearprop.service.UserFollowingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserFollowingController {
    
    private final UserFollowingService userFollowingService;
    
    @PostMapping("/{userId}/follow")
    public ResponseEntity<ApiResponse<UserDto>> followUser(@PathVariable Long userId) {
        log.info("Following user with ID: {}", userId);
        UserDto followedUser = userFollowingService.followUser(userId);
        return ResponseEntity.ok(
            ApiResponse.success("User followed successfully", followedUser)
        );
    }
    
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<ApiResponse<UserDto>> unfollowUser(@PathVariable Long userId) {
        log.info("Unfollowing user with ID: {}", userId);
        UserDto unfollowedUser = userFollowingService.unfollowUser(userId);
        return ResponseEntity.ok(
            ApiResponse.success("User unfollowed successfully", unfollowedUser)
        );
    }
    
    @GetMapping("/{userId}/following")
    public ResponseEntity<ApiResponse<Page<UserDto>>> getUserFollowing(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        log.info("Getting users followed by user with ID: {}", userId);
        
        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
                
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserDto> following = userFollowingService.getUserFollowing(userId, pageable);
        
        return ResponseEntity.ok(
            ApiResponse.success("Retrieved following list successfully", following)
        );
    }
    
    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponse<Page<UserDto>>> getUserFollowers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        log.info("Getting followers of user with ID: {}", userId);
        
        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
                
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserDto> followers = userFollowingService.getUserFollowers(userId, pageable);
        
        return ResponseEntity.ok(
            ApiResponse.success("Retrieved followers list successfully", followers)
        );
    }
    
    @GetMapping("/{userId}/follow/check")
    public ResponseEntity<ApiResponse<Boolean>> checkFollowStatus(@PathVariable Long userId) {
        log.info("Checking follow status for user with ID: {}", userId);
        boolean isFollowing = userFollowingService.isFollowing(userId);
        
        return ResponseEntity.ok(
            ApiResponse.success("Retrieved follow status successfully", isFollowing)
        );
    }
    
    @GetMapping("/{userId}/stats/following")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserFollowStats(@PathVariable Long userId) {
        log.info("Getting follow stats for user with ID: {}", userId);
        
        long followingCount = userFollowingService.getFollowingCount(userId);
        long followerCount = userFollowingService.getFollowerCount(userId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("followingCount", followingCount);
        stats.put("followerCount", followerCount);
        
        return ResponseEntity.ok(
            ApiResponse.success("Retrieved follow stats successfully", stats)
        );
    }
} 