package com.nearprop.service.impl;

import com.nearprop.dto.UserDto;
import com.nearprop.entity.User;
import com.nearprop.entity.UserFollowing;
import com.nearprop.exception.BadRequestException;
import com.nearprop.exception.EntityNotFoundException;
import com.nearprop.repository.UserFollowingRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.service.UserFollowingService;
import com.nearprop.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserFollowingServiceImpl implements UserFollowingService {

    private final UserFollowingRepository userFollowingRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    
    @Override
    @Transactional
    public UserDto followUser(Long followedId) {
        User currentUser = userService.getCurrentUser();
        
        // Prevent users from following themselves
        if (currentUser.getId().equals(followedId)) {
            throw new BadRequestException("You cannot follow yourself");
        }
        
        // Check if already following
        if (userFollowingRepository.existsByFollowerIdAndFollowedId(currentUser.getId(), followedId)) {
            throw new BadRequestException("You are already following this user");
        }
        
        User userToFollow = userRepository.findById(followedId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + followedId));
        
        // Create new following relationship
        UserFollowing following = UserFollowing.builder()
                .follower(currentUser)
                .followed(userToFollow)
                .build();
                
        userFollowingRepository.save(following);
        
        return mapToUserDto(userToFollow);
    }
    
    @Override
    @Transactional
    public UserDto unfollowUser(Long followedId) {
        User currentUser = userService.getCurrentUser();
        
        // Verify the user to unfollow exists
        User userToUnfollow = userRepository.findById(followedId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + followedId));
        
        // Check if following first
        if (!userFollowingRepository.existsByFollowerIdAndFollowedId(currentUser.getId(), followedId)) {
            throw new BadRequestException("You are not following this user");
        }
        
        // Delete following relationship
        userFollowingRepository.deleteByFollowerIdAndFollowedId(currentUser.getId(), followedId);
        
        return mapToUserDto(userToUnfollow);
    }
    
    @Override
    public boolean isFollowing(Long followedId) {
        User currentUser = userService.getCurrentUser();
        return userFollowingRepository.existsByFollowerIdAndFollowedId(currentUser.getId(), followedId);
    }
    
    @Override
    public Page<UserDto> getFollowing(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return getUserFollowing(currentUser.getId(), pageable);
    }
    
    @Override
    public Page<UserDto> getFollowers(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return getUserFollowers(currentUser.getId(), pageable);
    }
    
    @Override
    public Page<UserDto> getUserFollowing(Long userId, Pageable pageable) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        
        Page<UserFollowing> following = userFollowingRepository.findByFollowerId(userId, pageable);
        
        // Map to UserDto
        return mapUserFollowingPageToUserDtoPage(following, UserFollowing::getFollowed, pageable);
    }
    
    @Override
    public Page<UserDto> getUserFollowers(Long userId, Pageable pageable) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        
        Page<UserFollowing> followers = userFollowingRepository.findByFollowedId(userId, pageable);
        
        // Map to UserDto
        return mapUserFollowingPageToUserDtoPage(followers, UserFollowing::getFollower, pageable);
    }
    
    @Override
    public long getFollowerCount(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        
        return userFollowingRepository.countByFollowedId(userId);
    }
    
    @Override
    public long getFollowingCount(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        
        return userFollowingRepository.countByFollowerId(userId);
    }
    
    private Page<UserDto> mapUserFollowingPageToUserDtoPage(
            Page<UserFollowing> userFollowingPage,
            Function<UserFollowing, User> userExtractor,
            Pageable pageable) {
        
        var userDtos = userFollowingPage.getContent().stream()
                .map(userExtractor)
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
                
        return new PageImpl<>(userDtos, pageable, userFollowingPage.getTotalElements());
    }
    
    private UserDto mapToUserDto(User user) {
        User currentUser = userService.getCurrentUser();
        boolean isFollowing = false;
        
        if (currentUser != null) {
            isFollowing = userFollowingRepository.existsByFollowerIdAndFollowedId(
                    currentUser.getId(), user.getId());
        }
        
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getMobileNumber()) // Using mobileNumber as username
                .profileImageUrl(user.getProfileImageUrl())
                .isFollowing(isFollowing)
                .build();
    }
} 