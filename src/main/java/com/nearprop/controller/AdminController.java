//package com.nearprop.controller;
//
//import com.nearprop.dto.ApiResponse;
//import com.nearprop.dto.UserDto;
//import com.nearprop.dto.UserDetailDto;
//import com.nearprop.dto.admin.CreateAdminDto;
//import com.nearprop.entity.User;
//import com.nearprop.entity.Role;
//import com.nearprop.service.AdminService;
//import com.nearprop.service.UserManagementService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/v1/admin")
//@RequiredArgsConstructor
//@Slf4j
//public class AdminController {
//
//    private final AdminService adminService;
//    private final UserManagementService userManagementService;
//
//    /**
//     * Creates a new admin user
//     * Only existing admins can create other admin users
//     */
//    @PostMapping("/create")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<UserDto>> createAdmin(@Valid @RequestBody CreateAdminDto adminDto) {
//        log.info("Received request to create admin user with name: {}", adminDto.getName());
//
//        User adminUser = adminService.createAdmin(adminDto);
//
//        UserDto userDto = UserDto.builder()
//                .id(adminUser.getId())
//                .name(adminUser.getName())
//                .email(adminUser.getEmail())
//                .mobileNumber(adminUser.getMobileNumber())
//                .roles(adminUser.getRoles())
//                .build();
//
//        return ResponseEntity.ok(ApiResponse.success("Admin user created successfully", userDto));
//    }
//
//    /**
//     * Special endpoint for creating the first admin user in the system
//     * This endpoint should be secured through other means (IP restriction, special token, etc.)
//     */
//    @PostMapping("/setup")
//    public ResponseEntity<ApiResponse<UserDto>> setupFirstAdmin(
//            @RequestHeader("X-API-KEY") String apiKey,
//            @Valid @RequestBody CreateAdminDto adminDto) {
//
//        log.info("Received request to create first admin user");
//
//        // Check the API key (this should be a secure, non-guessable key)
//        if (!"4BgF8cPzQe2H7dRxKtYs9mNvJw5A3Z".equals(apiKey)) {
//            log.warn("Unauthorized attempt to create first admin with invalid API key");
//            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
//        }
//
//        User adminUser = adminService.createAdmin(adminDto);
//
//        UserDto userDto = UserDto.builder()
//                .id(adminUser.getId())
//                .name(adminUser.getName())
//                .email(adminUser.getEmail())
//                .mobileNumber(adminUser.getMobileNumber())
//                .roles(adminUser.getRoles())
//                .build();
//
//        return ResponseEntity.ok(ApiResponse.success("First admin user created successfully", userDto));
//    }
//
//    /**
//     * Get all users with detailed statistics
//     * Admin only endpoint
//     */
//    @GetMapping("/users")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<List<UserDetailDto>>> getAllUsersWithDetails() {
//        log.info("Admin request to get all users with detailed statistics");
//        List<UserDetailDto> users = userManagementService.getAllUsersWithDetails();
//        return ResponseEntity.ok(ApiResponse.success("All users retrieved successfully", users));
//    }
//
//    /**
//     * Get users by role with detailed statistics
//     * Admin only endpoint
//     */
//    @GetMapping("/users/role/{role}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'USER' ,'DEVELOPER','SELLER','ADVISOR')")
//    public ResponseEntity<ApiResponse<List<UserDetailDto>>> getUsersByRoleWithDetails(@PathVariable Role role) {
//        log.info("Admin request to get users with role: {} and detailed statistics", role);
//        List<UserDetailDto> users = userManagementService.getUsersByRoleWithDetails(role);
//        return ResponseEntity.ok(ApiResponse.success("Users with role " + role + " retrieved successfully", users));
//    }
//
//    /**
//     * Get specific user details with statistics
//     * Admin only endpoint
//     */
//    @GetMapping("/users/{userId}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<UserDetailDto>> getUserDetailsWithStatistics(@PathVariable Long userId) {
//        log.info("Admin request to get user details with statistics for user ID: {}", userId);
//        UserDetailDto userDetails = userManagementService.getUserDetailsWithStatistics(userId);
//        return ResponseEntity.ok(ApiResponse.success("User details retrieved successfully", userDetails));
//    }
//
//    // get Property Adviior which have 500 rupees subscription plan
//    @GetMapping("/active/advisior")
//    @PreAuthorize("hasAnyRole('ADMIN', 'USER' ,'DEVELOPER','SELLER','ADVISOR')")
//    public ResponseEntity<ApiResponse<?>> getPropertyAdvisior()
//    {
//        log.info("getting advisior which have 500 rupees subscription ");
//        List<UserDetailDto> users=userManagementService.getUsersByRoleHaveSubscription("ADVISIOR");
//        return ResponseEntity.ok(ApiResponse.success("Users with role  Property Advisior with subscription 500 retrieved successfully", users));
//    }
//
//    // get Property Adviior which have 500 rupees subscription plan
//    @GetMapping("/active/developer")
//    @PreAuthorize(("hasAnyRole('ADMIN','USER','DEVELOPER','SELLER','ADVISOR')"))
//    public ResponseEntity<ApiResponse<?>> getUsersByRoleHaveSubscription()
//    {
//        log.info("getting advisior which have 1500 rupees subscription ");
//        List<UserDetailDto> users=userManagementService.getUsersByRoleHaveDeveloperSubscription("DEVELOPER");
//        return ResponseEntity.ok(ApiResponse.success("Users with role  Property Advisior with subscription 1500 retrieved successfully",users));
//    }
//
//
//}


package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.UserDto;
import com.nearprop.dto.UserDetailDto;
import com.nearprop.dto.admin.CreateAdminDto;
import com.nearprop.entity.User;
import com.nearprop.entity.Role;
import com.nearprop.service.AdminService;
import com.nearprop.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;
    private final UserManagementService userManagementService;

    // ---------------- ADMIN CREATE ----------------

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> createAdmin(
            @Valid @RequestBody CreateAdminDto adminDto) {

        log.info("Received request to create admin user with name: {}", adminDto.getName());

        User adminUser = adminService.createAdmin(adminDto);

        UserDto userDto = UserDto.builder()
                .id(adminUser.getId())
                .name(adminUser.getName())
                .email(adminUser.getEmail())
                .mobileNumber(adminUser.getMobileNumber())
                .roles(adminUser.getRoles())
                .build();

        return ResponseEntity.ok(
                ApiResponse.success("Admin user created successfully", userDto)
        );
    }

    // ---------------- FIRST ADMIN SETUP ----------------

    @PostMapping("/setup")
    public ResponseEntity<ApiResponse<UserDto>> setupFirstAdmin(
            @RequestHeader("X-API-KEY") String apiKey,
            @Valid @RequestBody CreateAdminDto adminDto) {

        if (!"4BgF8cPzQe2H7dRxKtYs9mNvJw5A3Z".equals(apiKey)) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Unauthorized"));
        }

        User adminUser = adminService.createAdmin(adminDto);

        UserDto userDto = UserDto.builder()
                .id(adminUser.getId())
                .name(adminUser.getName())
                .email(adminUser.getEmail())
                .mobileNumber(adminUser.getMobileNumber())
                .roles(adminUser.getRoles())
                .build();

        return ResponseEntity.ok(
                ApiResponse.success("First admin user created successfully", userDto)
        );
    }

    // ---------------- GET ALL USERS (SAFE CHANGE) ----------------

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER','DEVELOPER','ADVISOR')")
    public ResponseEntity<ApiResponse<List<UserDetailDto>>> getAllUsersWithDetails() {

        log.info("Request to get all users with detailed statistics");

        List<UserDetailDto> users =
                userManagementService.getAllUsersWithDetails();

        return ResponseEntity.ok(
                ApiResponse.success("All users retrieved successfully", users)
        );
    }

    // ---------------- GET USERS BY ROLE ----------------

    @GetMapping("/users/role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN','USER','DEVELOPER','SELLER','ADVISOR')")
    public ResponseEntity<ApiResponse<List<UserDetailDto>>> getUsersByRoleWithDetails(
            @PathVariable Role role) {

        List<UserDetailDto> users =
                userManagementService.getUsersByRoleWithDetails(role);

        return ResponseEntity.ok(
                ApiResponse.success("Users with role " + role + " retrieved successfully", users)
        );
    }

    // ---------------- GET USER BY ID (SAFE CHANGE) ----------------

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER','DEVELOPER','ADVISOR')")
    public ResponseEntity<ApiResponse<UserDetailDto>> getUserDetailsWithStatistics(
            @PathVariable Long userId) {

        UserDetailDto userDetails =
                userManagementService.getUserDetailsWithStatistics(userId);

        return ResponseEntity.ok(
                ApiResponse.success("User details retrieved successfully", userDetails)
        );
    }

    // ---------------- SUBSCRIPTION ENDPOINTS ----------------

    @GetMapping("/active/advisior")
    @PreAuthorize("hasAnyRole('ADMIN','USER','DEVELOPER','SELLER','ADVISOR')")
    public ResponseEntity<ApiResponse<?>> getPropertyAdvisior() {

        List<UserDetailDto> users =
                userManagementService.getUsersByRoleHaveSubscription("ADVISIOR");

        return ResponseEntity.ok(
                ApiResponse.success("Active advisors retrieved successfully", users)
        );
    }

    @GetMapping("/active/developer")
    @PreAuthorize("hasAnyRole('ADMIN','USER','DEVELOPER','SELLER','ADVISOR')")
    public ResponseEntity<ApiResponse<?>> getActiveDevelopers() {

        List<UserDetailDto> users =
                userManagementService.getUsersByRoleHaveDeveloperSubscription("DEVELOPER");

        return ResponseEntity.ok(
                ApiResponse.success("Active developers retrieved successfully", users)
        );
    }
}
