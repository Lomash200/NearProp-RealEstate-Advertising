package com.nearprop.service.impl;

import com.nearprop.dto.SubAdminDetailWithPermissionsDto;
import com.nearprop.dto.UserDetailDto;
import com.nearprop.entity.Role;
import com.nearprop.entity.SubAdminPermission;
import com.nearprop.entity.User;
import com.nearprop.enums.Action;
import com.nearprop.enums.PermissionUser;
import com.nearprop.exception.EntityNotFoundException;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.repository.*;
import com.nearprop.repository.franchisee.FranchiseeDistrictRepository;
import com.nearprop.advertisement.repository.AdvertisementRepository;
import com.nearprop.service.SubAdminPermissionService;
import com.nearprop.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import com.nearprop.repository.DistrictRepository;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final ReelRepository reelRepository;
    private final ReviewRepository reviewRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RoleRequestRepository roleRequestRepository;
    private final FranchiseeDistrictRepository franchiseeDistrictRepository;
    private final AdvertisementRepository advertisementRepository;
    private final DistrictRepository districtRepository;



    @Override
    public List<UserDetailDto> getUsersByRoleHaveDeveloperSubscription(String developer) {
        log.info("Fetching users having active Advisor subscription + Advisor role");
        Long DEVELOPER_PLAN_ID = 5L;
        List<User> users = subscriptionRepository
                .findUsersByActiveSubscriptionPlan(
                        DEVELOPER_PLAN_ID,
                        LocalDateTime.now()
                );
        return users.stream()
                .filter(user -> user.getRoles().contains(Role.DEVELOPER)) // ✅ SAFE ROLE CHECK
                .map(this::mapToUserDetailDto)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public UserDetailDto getUserDetailById(Long userId) {
        log.info("Fetching user details for ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (!user.getRoles().contains(Role.SUBADMIN)) {
            log.warn("User with ID: {} is not a SUBADMIN", userId);
            throw new ResourceNotFoundException("SubAdmin not found with ID: " + userId);
        }

        return mapToUserDetailDto(user);
    }

    @Override
    public List<UserDetailDto> getUsersByRoleHaveSubscription(String advisor) {

        log.info("Fetching users having active Advisor subscription + Advisor role");
        Long ADVISOR_PLAN_ID = 4L;

        List<User> users = subscriptionRepository
                .findUsersByActiveSubscriptionPlan(
                        ADVISOR_PLAN_ID,
                        LocalDateTime.now()
                );

        return users.stream()
                .filter(user -> user.getRoles().contains(Role.ADVISOR)) // ✅ SAFE ROLE CHECK
                .map(this::mapToUserDetailDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<UserDetailDto> getAllUsersWithDetails() {
        log.info("Fetching all users with detailed statistics");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToUserDetailDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDetailDto> getUsersByRoleWithDetails(Role role) {
        log.info("Fetching users with role: {} and detailed statistics", role);
        List<User> users = userRepository.findAllByRole(role);
        return users.stream()
                .map(this::mapToUserDetailDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailDto getUserDetailsWithStatistics(Long userId) {
        log.info("Fetching user details with statistics for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        return mapToUserDetailDto(user);
    }

    private UserDetailDto mapToUserDetailDto(User user) {
        Long userId = user.getId();

        // Get property statistics
        Long propertyCount = propertyRepository.countByOwnerId(userId);
        Long activePropertyCount = (long) propertyRepository.countByOwnerIdAndActiveTrue(userId);
        Long pendingPropertyCount = propertyRepository.countByOwnerIdAndApprovedFalse(userId);

        // Get reel statistics
        Long reelCount = reelRepository.countReelsByUserId(userId);

        // Get review statistics
        Long reviewCount = reviewRepository.countByUserId(userId);

        // Get subscription statistics
        Long subscriptionCount = subscriptionRepository.countByUserId(userId);
        Long activeSubscriptionCount = subscriptionRepository.countByUserIdAndStatusActive(userId);

        // Get chat room statistics
        Long chatRoomCount = chatRoomRepository.countByBuyerIdOrSellerId(userId, userId);

        // Get advertisement statistics
        Long advertisementCount = advertisementRepository.countByCreatedById(userId);

        // Get role request statistics
        Long roleRequestCount = roleRequestRepository.countByUserId(userId);
        Long pendingRoleRequestCount = roleRequestRepository.countByUserIdAndStatusPending(userId);

        // Get favorite count (this would need a separate repository method)
        Long favoriteCount = 0L; // TODO: Implement if needed

//        return UserDetailDto.builder()
//                .id(user.getId())
//                .permanentId(user.getPermanentId())
//                .name(user.getName())
//                .email(user.getEmail())
//                .mobileNumber(user.getMobileNumber())
//                .address(user.getAddress())
//                .district(user.getDistrict())
//                .profileImageUrl(user.getProfileImageUrl())
//                .roles(user.getRoles())
//                .mobileVerified(user.isMobileVerified())
//                .emailVerified(user.isEmailVerified())
//                .aadhaarNumber(user.getAadhaarNumber())
//                .aadhaarVerified(user.isAadhaarVerified())
//                .aadhaarDocumentUrl(user.getAadhaarDocumentUrl())
//                .createdAt(user.getCreatedAt())
//                .updatedAt(user.getUpdatedAt())
//                .lastLoginAt(user.getLastLoginAt())
//                .propertyCount(propertyCount)
//                .activePropertyCount(activePropertyCount)
//                .pendingPropertyCount(pendingPropertyCount)
//                .reelCount(reelCount)
//                .favoriteCount(favoriteCount)
//                .reviewCount(reviewCount)
//                .subscriptionCount(subscriptionCount)
//                .activeSubscriptionCount(activeSubscriptionCount)
//                .chatRoomCount(chatRoomCount)
//                .advertisementCount(advertisementCount)
//                .roleRequestCount(roleRequestCount)
//                .pendingRoleRequestCount(pendingRoleRequestCount)
//                .build();

        UserDetailDto dto = UserDetailDto.builder()
                .id(user.getId())
                .permanentId(user.getPermanentId())
                .name(user.getName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .address(user.getAddress())
                // ❌ districtId REMOVED from DTO
                .profileImageUrl(user.getProfileImageUrl())
                .roles(user.getRoles())
                .mobileVerified(user.isMobileVerified())
                .emailVerified(user.isEmailVerified())
                .aadhaarNumber(user.getAadhaarNumber())
                .aadhaarVerified(user.isAadhaarVerified())
                .aadhaarDocumentUrl(user.getAadhaarDocumentUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .propertyCount(propertyCount)
                .activePropertyCount(activePropertyCount)
                .pendingPropertyCount(pendingPropertyCount)
                .reelCount(reelCount)
                .favoriteCount(favoriteCount)
                .reviewCount(reviewCount)
                .subscriptionCount(subscriptionCount)
                .activeSubscriptionCount(activeSubscriptionCount)
                .chatRoomCount(chatRoomCount)
                .advertisementCount(advertisementCount)
                .roleRequestCount(roleRequestCount)
                .pendingRoleRequestCount(pendingRoleRequestCount)
                .build();

// ✅ district & state resolved ONLY from District master
        if (user.getDistrictId() != null) {
            districtRepository.findById(user.getDistrictId())
                    .ifPresent(district -> {
                        dto.setDistrict(district.getName());
                        dto.setState(district.getState());
                    });
        }

        return dto;


    }
} 