//package com.nearprop.service.impl;
//
//import com.nearprop.dto.UserDetailDto;
//import com.nearprop.dto.UserDto;
//import com.nearprop.entity.Role;
//import com.nearprop.entity.Subscription;
//import com.nearprop.entity.SubscriptionPlan;
//import com.nearprop.entity.User;
//import com.nearprop.exception.EntityNotFoundException;
//import com.nearprop.repository.UserRepository;
//import com.nearprop.service.UserService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//import com.nearprop.repository.DistrictRepository;
//
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class UserServiceImpl implements UserService {
//
//    private final UserRepository userRepository;
//    private final DistrictRepository districtRepository;
//
//    @Override
//    public Optional<User> findByUsername(String username) {
//        return userRepository.findByUsername(username);
//    }
//
//    @Override
//    public User getCurrentUser() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        // In testing mode, the principal is set to the User object directly
//        if (principal instanceof User) {
//            return (User) principal;
//        }
//
//        // Normal flow - look up user by username
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        return userRepository.findByUsername(username)
//                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
//    }
//
//    @Override
//    public UserDto getUserDetails(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
//
//        return mapToUserDto(user);
//    }
//
//    @Override
//    public List<UserDto> getAllUsers() {
//        return userRepository.findAll().stream()
//                .map(this::mapToUserDto)
//                .collect(Collectors.toList());
//    }
//
////    public List<UserDetailDto>  getPublicDevelopers() {
////
////        List<User> users = userRepository.findAllByRole(Role.DEVELOPER);
////
////        return users.stream()
////                .map(this::buildUserDetailDto) // existing mapper
////                .toList();
////    }
//
//    @Override
//    public List<UserDetailDto> getPublicDevelopers() {
//
//        List<User> users =
//                userRepository.findPublicUsersWithActiveProfile(
//                        Role.DEVELOPER,
//                        SubscriptionPlan.PlanType.PROFILE,
//                        Subscription.SubscriptionStatus.ACTIVE
//                );
//
//        return users.stream()
//                .map(this::buildUserDetailDto)
//                .toList();
//    }
//
//
//
//
//
//    //    private UserDto mapToUserDto(User user) {
////        return UserDto.builder()
////                .id(user.getId())
////                .name(user.getName())
////                .email(user.getEmail())
////                .mobileNumber(user.getMobileNumber())
////                .username(user.getUsername())
////                .phoneNumber(user.getMobileNumber())
////                .profileImageUrl(user.getProfileImageUrl())
////                .roles(user.getRoles())
////                .build();
////    }
//private UserDto mapToUserDto(User user) {
//
//    UserDto dto = UserDto.builder()
//            .id(user.getId())
//            .permanentId(user.getPermanentId())
//            .name(user.getName())
//            .email(user.getEmail())
//            .mobileNumber(user.getMobileNumber())
//            .username(user.getUsername())
//            .phoneNumber(user.getMobileNumber())
//            .profileImageUrl(user.getProfileImageUrl())
//            .roles(user.getRoles())
//            .latitude(user.getLatitude())
//            .longitude(user.getLongitude())
//            .districtId(user.getDistrictId())
//            .createdAt(user.getCreatedAt())
//            .updatedAt(user.getUpdatedAt())
//            .build();
//
//    // ✅ district (direct from User table)
//    dto.setDistrict(user.getDistrict());
//
//    // ✅ state (via districtId → District table)
//    if (user.getDistrictId() != null) {
//        districtRepository.findById(user.getDistrictId())
//                .ifPresent(district -> dto.setState(district.getState()));
//    }
//
//    return dto;
//}
//
//
//
//    private UserDetailDto buildUserDetailDto(User user) {
//
//        UserDetailDto dto = UserDetailDto.builder()
//                .id(user.getId())
//                .permanentId(user.getPermanentId())
//                .name(user.getName())
//                .email(user.getEmail())
//                .mobileNumber(user.getMobileNumber())
//                .profileImageUrl(user.getProfileImageUrl())
//                .roles(user.getRoles())
//                .mobileVerified(user.isMobileVerified())
//                .emailVerified(user.isEmailVerified())
//                .aadhaarVerified(user.isAadhaarVerified())
//                .createdAt(user.getCreatedAt())
//                .updatedAt(user.getUpdatedAt())
//                .build();
//
//        // ✅ CORRECT: district + state BOTH from District table
//        if (user.getDistrictId() != null) {
//            districtRepository.findById(user.getDistrictId())
//                    .ifPresent(district -> {
//                        dto.setDistrict(district.getName());   // ✅ THIS WAS MISSING
//                        dto.setState(district.getState());
//                    });
//        }
//
//        return dto;
//    }
//
//    @Override
//    public List<UserDetailDto> getPublicAdvisors() {
//
//        List<User> users =
//                userRepository.findPublicUsersWithActiveProfile(
//                        Role.ADVISOR,
//                        SubscriptionPlan.PlanType.PROFILE,
//                        Subscription.SubscriptionStatus.ACTIVE
//                );
//
//        return users.stream()
//                .map(this::buildUserDetailDto)
//                .toList();
//    }
//
//
//
//    @Override
//    public List<UserDto> getUsersByRole(Role role) {
//        return userRepository.findAllByRole(role).stream()
//                .map(this::mapToUserDto)
//                .toList();
//    }
//
//
//}

package com.nearprop.service.impl;

import com.nearprop.dto.UserDetailDto;
import com.nearprop.dto.UserDto;
import com.nearprop.entity.Role;
import com.nearprop.entity.Subscription;
import com.nearprop.entity.SubscriptionPlan;
import com.nearprop.entity.User;
import com.nearprop.exception.EntityNotFoundException;
import com.nearprop.repository.UserRepository;
import com.nearprop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.nearprop.repository.DistrictRepository;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DistrictRepository districtRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // In testing mode, the principal is set to the User object directly
        if (principal instanceof User) {
            return (User) principal;
        }

        // Normal flow - look up user by username
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
    }

    @Override
    public UserDto getUserDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        return mapToUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }


//    @Override
//    public List<UserDetailDto> getPublicAdvisors() {
//
//        List<User> users =
//                userRepository.findActiveUsersByRoleAndPlanType(
//                        Role.ADVISOR,
//                        Subscription.SubscriptionStatus.ACTIVE,
//                        "PROFILE"
//                );
//
//        return users.stream()
//                .map(this::buildUserDetailDto)
//                .toList();
//    }
//
//    @Override
//    public List<UserDetailDto> getPublicDevelopers() {
//
//        List<User> users =
//                userRepository.findActiveUsersByRoleAndPlanType(
//                        Role.DEVELOPER,
//                        Subscription.SubscriptionStatus.ACTIVE,
//                        "PROFILE"
//                );
//
//        return users.stream()
//                .map(this::buildUserDetailDto)
//                .toList();
//    }

    @Override
    public List<UserDetailDto> getPublicDevelopers() {

        List<User> users =
                userRepository.findUsersHavingActiveProfileSubscription(
                        Role.DEVELOPER,
                        Subscription.SubscriptionStatus.ACTIVE,
                        "PROFILE"
                );

        return users.stream()
                .map(this::buildUserDetailDto)
                .toList();
    }

    @Override
    public List<UserDetailDto> getPublicAdvisors() {

        List<User> users =
                userRepository.findUsersHavingActiveProfileSubscription(
                        Role.ADVISOR,
                        Subscription.SubscriptionStatus.ACTIVE,
                        "PROFILE"
                );

        return users.stream()
                .map(this::buildUserDetailDto)
                .toList();
    }



    private UserDto mapToUserDto(User user) {
        UserDto dto = UserDto.builder()
                .id(user.getId())
                .permanentId(user.getPermanentId())
                .name(user.getName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .username(user.getUsername())
                .phoneNumber(user.getMobileNumber())
                .profileImageUrl(user.getProfileImageUrl())
                .roles(user.getRoles())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .districtId(user.getDistrictId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        // ✅ district (direct from User table)
        dto.setDistrict(user.getDistrict());

        // ✅ state (via districtId → District table)
        if (user.getDistrictId() != null) {
            districtRepository.findById(user.getDistrictId())
                    .ifPresent(district -> dto.setState(district.getState()));
        }

        return dto;
    }

    private UserDetailDto buildUserDetailDto(User user) {
        UserDetailDto dto = UserDetailDto.builder()
                .id(user.getId())
                .permanentId(user.getPermanentId())
                .name(user.getName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .profileImageUrl(user.getProfileImageUrl())
                .roles(user.getRoles())
                .mobileVerified(user.isMobileVerified())
                .emailVerified(user.isEmailVerified())
                .aadhaarVerified(user.isAadhaarVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        // ✅ CORRECT: district + state BOTH from District table
        if (user.getDistrictId() != null) {
            districtRepository.findById(user.getDistrictId())
                    .ifPresent(district -> {
                        dto.setDistrict(district.getName());
                        dto.setState(district.getState());
                    });
        }

        return dto;
    }

    @Override
    public List<UserDto> getUsersByRole(Role role) {
        return userRepository.findAllByRole(role).stream()
                .map(this::mapToUserDto)
                .toList();
    }
}