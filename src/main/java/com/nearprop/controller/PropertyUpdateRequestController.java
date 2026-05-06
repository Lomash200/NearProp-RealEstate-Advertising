package com.nearprop.controller;

import com.nearprop.dto.CreatePropertyUpdateRequestDto;
import com.nearprop.dto.PropertyUpdateRequestDto;
import com.nearprop.dto.ReviewPropertyUpdateRequestDto;
import com.nearprop.entity.PropertyUpdateRequest.RequestStatus;
import com.nearprop.entity.User;
import com.nearprop.service.PropertyUpdateRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/property-updates")
@RequiredArgsConstructor
@Slf4j
public class PropertyUpdateRequestController {

    private final PropertyUpdateRequestService updateRequestService;
    
    /**
     * Create a new property update request
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADVISOR', 'DEVELOPER')")
    public ResponseEntity<PropertyUpdateRequestDto> createUpdateRequest(
            @ModelAttribute CreatePropertyUpdateRequestDto requestDto,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "video", required = false) MultipartFile videoFile,
            @AuthenticationPrincipal User currentUser) {
        
        PropertyUpdateRequestDto createdRequest = updateRequestService.createUpdateRequest(
                requestDto, images, videoFile, currentUser.getId());
        
        return ResponseEntity.ok(createdRequest);
    }
    
    /**
     * Get a property update request by ID
     */
    @GetMapping("/{requestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PropertyUpdateRequestDto> getUpdateRequest(
            @PathVariable Long requestId) {
        
        PropertyUpdateRequestDto request = updateRequestService.getUpdateRequest(requestId);
        return ResponseEntity.ok(request);
    }
    
    /**
     * Get a property update request by permanent ID
     */
    @GetMapping("/by-permanent-id/{requestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PropertyUpdateRequestDto> getUpdateRequestByPermanentId(
            @PathVariable String requestId) {
        
        PropertyUpdateRequestDto request = updateRequestService.getUpdateRequestByPermanentId(requestId);
        return ResponseEntity.ok(request);
    }
    
    /**
     * Get all property update requests for a property
     */
    @GetMapping("/property/{propertyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PropertyUpdateRequestDto>> getUpdateRequestsByProperty(
            @PathVariable Long propertyId,
            Pageable pageable) {
        
        Page<PropertyUpdateRequestDto> requests = updateRequestService.getUpdateRequestsByProperty(propertyId, pageable);
        return ResponseEntity.ok(requests);
    }
    
    /**
     * Get all property update requests created by the current user
     */
    @GetMapping("/my-requests")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PropertyUpdateRequestDto>> getMyUpdateRequests(
            @AuthenticationPrincipal User currentUser,
            Pageable pageable) {
        
        Page<PropertyUpdateRequestDto> requests = updateRequestService.getUpdateRequestsByUser(
                currentUser.getId(), pageable);
        
        return ResponseEntity.ok(requests);
    }
    
    /**
     * Cancel a property update request
     */
    @PostMapping("/{requestId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PropertyUpdateRequestDto> cancelUpdateRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal User currentUser) {
        
        PropertyUpdateRequestDto cancelledRequest = updateRequestService.cancelUpdateRequest(
                requestId, currentUser.getId());
        
        return ResponseEntity.ok(cancelledRequest);
    }
    
    /**
     * Admin endpoints
     */
    
    /**
     * Get all pending property update requests (admin only)
     */
    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PropertyUpdateRequestDto>> getPendingUpdateRequests(Pageable pageable) {
        Page<PropertyUpdateRequestDto> requests = updateRequestService.getUpdateRequestsByStatus(
                RequestStatus.PENDING, pageable);
        
        return ResponseEntity.ok(requests);
    }
    
    /**
     * Get all pending property update requests that need admin review
     */
    @GetMapping("/admin/pending-review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PropertyUpdateRequestDto>> getPendingAdminReviewRequests(Pageable pageable) {
        Page<PropertyUpdateRequestDto> requests = updateRequestService.getPendingRequestsForAdminReview(pageable);
        
        return ResponseEntity.ok(requests);
    }
    
    /**
     * Review a property update request (admin only)
     */
    @PostMapping("/admin/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PropertyUpdateRequestDto> reviewUpdateRequest(
            @RequestBody ReviewPropertyUpdateRequestDto reviewDto,
            @AuthenticationPrincipal User currentUser) {
        
        // Set reviewer type to ADMIN
        reviewDto.setReviewerType(ReviewPropertyUpdateRequestDto.ReviewerType.ADMIN);
        
        PropertyUpdateRequestDto reviewedRequest = updateRequestService.reviewUpdateRequest(
                reviewDto, currentUser.getId());
        
        return ResponseEntity.ok(reviewedRequest);
    }
} 