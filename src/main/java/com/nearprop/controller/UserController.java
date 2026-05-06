package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.UserDto;
import com.nearprop.entity.User;
import com.nearprop.entity.Role;
import com.nearprop.repository.UserRepository;
import com.nearprop.repository.UserSessionRepository;
import com.nearprop.service.S3Service;
import com.nearprop.repository.franchisee.FranchiseeDistrictRepository;
import com.nearprop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.nearprop.dto.UserLocationDto;
import org.springframework.http.MediaType;
import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final S3Service s3Service;
    private final FranchiseeDistrictRepository franchiseeDistrictRepository;
    private final UserService userService;


    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> getProfile(@AuthenticationPrincipal User currentUser) {
        log.info("Fetching profile for user {}", currentUser.getId());
        
        UserDto userDto = UserDto.builder()
                .id(currentUser.getId())
                .permanentId(currentUser.getPermanentId())
                .name(currentUser.getName())
                .email(currentUser.getEmail())
                .mobileNumber(currentUser.getMobileNumber())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .roles(currentUser.getRoles())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", userDto));
    }
    
    @PostMapping("/profile/image")
    public ResponseEntity<ApiResponse<UserDto>> uploadProfileImage(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("image") MultipartFile image) {
        log.info("Uploading profile image for user {}", currentUser.getId());
        
        if (image.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Image file cannot be empty"));
        }
        
        String imageUrl = s3Service.uploadProfileImage(image, currentUser);
        
        // Update user profile with new image URL
        currentUser.setProfileImageUrl(imageUrl);
        User updatedUser = userRepository.save(currentUser);
        
        UserDto userDto = UserDto.builder()
                .id(updatedUser.getId())
                .permanentId(updatedUser.getPermanentId())
                .name(updatedUser.getName())
                .email(updatedUser.getEmail())
                .mobileNumber(updatedUser.getMobileNumber())
                .profileImageUrl(updatedUser.getProfileImageUrl())
                .roles(updatedUser.getRoles())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("Profile image uploaded successfully", userDto));
    }
    
    @PostMapping("/location")
    public ResponseEntity<ApiResponse<UserDto>> updateUserLocation(@AuthenticationPrincipal User currentUser, @RequestBody UserLocationDto locationDto) {
        log.info("Updating location for user {}: lat={}, lng={}, districtId={}", currentUser.getId(), locationDto.getLatitude(), locationDto.getLongitude(), locationDto.getDistrictId());
        currentUser.setLatitude(locationDto.getLatitude());
        currentUser.setLongitude(locationDto.getLongitude());
        currentUser.setDistrictId(locationDto.getDistrictId());
        User updatedUser = userRepository.save(currentUser);
        UserDto userDto = UserDto.builder()
                .id(updatedUser.getId())
                .permanentId(updatedUser.getPermanentId())
                .name(updatedUser.getName())
                .email(updatedUser.getEmail())
                .mobileNumber(updatedUser.getMobileNumber())
                .profileImageUrl(updatedUser.getProfileImageUrl())
                .roles(updatedUser.getRoles())
                .latitude(updatedUser.getLatitude())
                .longitude(updatedUser.getLongitude())
                .districtId(updatedUser.getDistrictId())
                .build();
        return ResponseEntity.ok(ApiResponse.success("User location updated successfully", userDto));
    }
    
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long userId) {
        log.info("Fetching user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .permanentId(user.getPermanentId())
                .name(user.getName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .profileImageUrl(user.getProfileImageUrl())
                .roles(user.getRoles())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", userDto));
    }
    
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        log.info("Admin request to delete user with ID: {}", userId);
        
        // Check if user exists before attempting deletion
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.ok(ApiResponse.error("User not available"));
        }
        
        try {
            // First delete related records from user_sessions table
            deleteUserSessions(userId);
            
            // Then delete the user
            userRepository.deleteById(userId);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting user with ID: {}", userId, e);
            return ResponseEntity.ok(ApiResponse.error("Failed to delete user: " + e.getMessage()));
        }
    }
    
    private void deleteUserSessions(Long userId) {
        log.info("Deleting all sessions for user with ID: {}", userId);
        // Use native query to bypass foreign key constraints
        userSessionRepository.deleteAllByUserId(userId);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FRANCHISE')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        log.info("Admin request to get all users");
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = users.stream()
                .map(user -> UserDto.builder()
                        .id(user.getId())
                        .permanentId(user.getPermanentId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .mobileNumber(user.getMobileNumber())
                        .profileImageUrl(user.getProfileImageUrl())
                        .roles(user.getRoles())
                        .build())
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("All users retrieved successfully", userDtos));
    }
    
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsersByRole(@PathVariable String role) {
        log.info("Admin request to get users with role: {}", role);
        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            List<UserDto> users = userService.getUsersByRole(roleEnum);

            List<UserDto> userDtos = users.stream()
                    .map(user -> UserDto.builder()
                            .id(user.getId())
                            .permanentId(user.getPermanentId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .mobileNumber(user.getMobileNumber())
                            .profileImageUrl(user.getProfileImageUrl())
                            .roles(user.getRoles())
                            .build())
                    .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Users with role " + role + " retrieved successfully", userDtos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid role: " + role));
        }
    }

    @GetMapping("/district/users")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsersOfFranchiseeDistricts(@AuthenticationPrincipal User currentUser) {
        log.info("Franchisee request to get all users of their districts");
        // 1. Get all districts managed by this franchisee
        List<com.nearprop.entity.FranchiseeDistrict> franchiseeDistricts = franchiseeDistrictRepository.findByUserId(currentUser.getId());
        if (franchiseeDistricts.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("No districts assigned to this franchisee", List.of()));
        }
        // 2. Collect all district names (or IDs if needed)
        List<String> districtNames = franchiseeDistricts.stream()
                .map(com.nearprop.entity.FranchiseeDistrict::getDistrictName)
                .toList();
        // 3. Find all users whose district matches any of these district names
        List<User> users = userRepository.findByDistrictIn(districtNames);
        List<UserDto> userDtos = users.stream()
                .map(user -> UserDto.builder()
                        .id(user.getId())
                        .permanentId(user.getPermanentId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .mobileNumber(user.getMobileNumber())
                        .profileImageUrl(user.getProfileImageUrl())
                        .roles(user.getRoles())
                        .build())
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("All users of franchisee's districts retrieved successfully", userDtos));
    }

  @PutMapping(value = "/profile-update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserDto>> updateProfileForm(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        // Update name
        if (name != null && !name.isBlank()) {
            currentUser.setName(name);
        }

        // Update mobile number
        if (mobileNumber != null && !mobileNumber.isBlank()) {
            if (userRepository.existsByMobileNumber(mobileNumber)
                    && !mobileNumber.equals(currentUser.getMobileNumber())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Mobile number already in use"));
            }
            currentUser.setMobileNumber(mobileNumber);
        }

        // Update email
        if (email != null && !email.isBlank()) {
            if (userRepository.existsByEmail(email)
                    && !email.equals(currentUser.getEmail())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Email already in use"));
            }
            currentUser.setEmail(email);
        }

        // Update profile image
        if (image != null && !image.isEmpty()) {
            String imageUrl = s3Service.uploadProfileImage(image, currentUser);
            currentUser.setProfileImageUrl(imageUrl);
        }

        // Save updated user
        User updatedUser = userRepository.save(currentUser);

        // Map to DTO
        UserDto userDto = UserDto.builder()
                .id(updatedUser.getId())
                .permanentId(updatedUser.getPermanentId())
                .name(updatedUser.getName())
                .email(updatedUser.getEmail())
                .mobileNumber(updatedUser.getMobileNumber())
                .profileImageUrl(updatedUser.getProfileImageUrl())
                .roles(updatedUser.getRoles())
                .latitude(updatedUser.getLatitude())
                .longitude(updatedUser.getLongitude())
                .districtId(updatedUser.getDistrictId())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", userDto));
    }

} 
