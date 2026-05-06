package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.role.ProcessRoleRequestDto;
import com.nearprop.dto.role.RoleRequestDto;
import com.nearprop.entity.Role;
import com.nearprop.entity.RoleRequest;
import com.nearprop.entity.User;
import com.nearprop.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {
    private final RoleService roleService;

    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse<List<RoleRequest>>> getMyRequests(
            @AuthenticationPrincipal User user) {
        log.info("Fetching role requests for user {}", user.getId());
        List<RoleRequest> requests = roleService.getUserRequests(user.getId());
        return ResponseEntity.ok(ApiResponse.success("User role requests retrieved successfully", requests));
    }

    @PostMapping("/{userId}/request")
    public ResponseEntity<ApiResponse<RoleRequest>> requestRole(
            @PathVariable Long userId,
            @Valid @RequestBody RoleRequestDto request) {
        log.info("Received role request for user {} to role {}", userId, request.getRole());
        RoleRequest roleRequest = roleService.requestRole(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Role request submitted successfully", roleRequest));
    }

    @PostMapping("/requests/{requestId}/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoleRequest>> processRoleRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody ProcessRoleRequestDto request) {
        log.info("Processing role request {}", requestId);
        RoleRequest roleRequest = roleService.processRoleRequest(requestId, request);
        return ResponseEntity.ok(ApiResponse.success("Role request processed successfully", roleRequest));
    }

    @GetMapping("/requests/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<RoleRequest>>> getPendingRequests(Pageable pageable) {
        log.info("Fetching pending role requests");
        Page<RoleRequest> requests = roleService.getPendingRequests(pageable);
        return ResponseEntity.ok(ApiResponse.success("Pending requests retrieved successfully", requests));
    }

    @PostMapping("/{userId}/add/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> addRole(
            @PathVariable Long userId,
            @PathVariable Role role) {
        log.info("Adding role {} to user {}", role, userId);
        roleService.addRole(userId, role);
        return ResponseEntity.ok(ApiResponse.success("Role added successfully"));
    }

    @PostMapping("/{userId}/remove/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeRole(
            @PathVariable Long userId,
            @PathVariable Role role) {
        log.info("Removing role {} from user {}", role, userId);
        roleService.removeRole(userId, role);
        return ResponseEntity.ok(ApiResponse.success("Role removed successfully"));
    }

    @GetMapping("/{userId}/has/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> hasRole(
            @PathVariable Long userId,
            @PathVariable Role role) {
        log.info("Checking if user {} has role {}", userId, role);
        boolean hasRole = roleService.hasRole(userId, role);
        return ResponseEntity.ok(ApiResponse.success(
                hasRole ? "User has the role" : "User does not have the role",
                hasRole
        ));
    }
} 