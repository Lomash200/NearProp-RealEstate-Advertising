package com.nearprop.controller.franchisee;

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
@RequestMapping("/franchisee/property-updates")
@RequiredArgsConstructor
@Slf4j
public class FranchiseePropertyUpdateController {

    private final PropertyUpdateRequestService updateRequestService;
    
    /**
     * Create a new property update request on behalf of a property owner
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<PropertyUpdateRequestDto> createUpdateRequestOnBehalf(
            @RequestParam String ownerPermanentId,
            @ModelAttribute CreatePropertyUpdateRequestDto requestDto,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "video", required = false) MultipartFile videoFile,
            @AuthenticationPrincipal User currentUser) {
        
        PropertyUpdateRequestDto createdRequest = updateRequestService.createUpdateRequestOnBehalf(
                ownerPermanentId, requestDto, images, videoFile, currentUser.getId());
        
        return ResponseEntity.ok(createdRequest);
    }
    
    /**
     * Get all property update requests created by the franchisee
     */
    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<Page<PropertyUpdateRequestDto>> getMyUpdateRequests(
            @AuthenticationPrincipal User currentUser,
            Pageable pageable) {
        
        Page<PropertyUpdateRequestDto> requests = updateRequestService.getUpdateRequestsByUser(
                currentUser.getId(), pageable);
        
        return ResponseEntity.ok(requests);
    }
    
    /**
     * Get all pending property update requests in the franchisee's district
     */
    @GetMapping("/district/pending")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<Page<PropertyUpdateRequestDto>> getDistrictPendingRequests(
            @AuthenticationPrincipal User currentUser,
            Pageable pageable) {
        
        String district = currentUser.getDistrict();
        if (district == null || district.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Page<PropertyUpdateRequestDto> requests = updateRequestService.getUpdateRequestsByDistrictAndStatus(
                district, RequestStatus.PENDING, pageable);
        
        return ResponseEntity.ok(requests);
    }
    
    /**
     * Get all pending property update requests that need franchisee review in their district
     */
    @GetMapping("/district/pending-review")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<Page<PropertyUpdateRequestDto>> getPendingFranchiseeReviewRequests(
            @AuthenticationPrincipal User currentUser,
            Pageable pageable) {
        
        String district = currentUser.getDistrict();
        if (district == null || district.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Page<PropertyUpdateRequestDto> requests = updateRequestService.getPendingRequestsForFranchiseeReview(
                district, pageable);
        
        return ResponseEntity.ok(requests);
    }
    
    /**
     * Review a property update request (franchisee only)
     */
    @PostMapping("/review")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<PropertyUpdateRequestDto> reviewUpdateRequest(
            @RequestBody ReviewPropertyUpdateRequestDto reviewDto,
            @AuthenticationPrincipal User currentUser) {
        
        // Set reviewer type to FRANCHISEE
        reviewDto.setReviewerType(ReviewPropertyUpdateRequestDto.ReviewerType.FRANCHISEE);
        
        PropertyUpdateRequestDto reviewedRequest = updateRequestService.reviewUpdateRequest(
                reviewDto, currentUser.getId());
        
        return ResponseEntity.ok(reviewedRequest);
    }
    
    /**
     * Cancel a property update request
     */
    @PostMapping("/{requestId}/cancel")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<PropertyUpdateRequestDto> cancelUpdateRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal User currentUser) {
        
        PropertyUpdateRequestDto cancelledRequest = updateRequestService.cancelUpdateRequest(
                requestId, currentUser.getId());
        
        return ResponseEntity.ok(cancelledRequest);
    }
} 