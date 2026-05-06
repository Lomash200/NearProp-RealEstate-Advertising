package com.nearprop.dto;

import com.nearprop.entity.PropertyUpdateRequest.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyUpdateRequestDto {
    
    private Long id;
    private String requestId;
    private Long propertyId;
    private String propertyTitle;
    private String propertyPermanentId;
    private UserSummaryDto requestedBy;
    private UserSummaryDto reviewedByAdmin;
    private UserSummaryDto reviewedByFranchisee;
    private RequestStatus status;
    private String requestNotes;
    private String adminNotes;
    private String franchiseeNotes;
    private String rejectionReason;
    private String district;
    private boolean adminReviewed;
    private boolean franchiseeReviewed;
    private Boolean adminApproved;
    private Boolean franchiseeApproved;
    private Map<String, String> oldValues;
    private Map<String, String> newValues;
    private LocalDateTime submittedAt;
    private LocalDateTime adminReviewedAt;
    private LocalDateTime franchiseeReviewedAt;
    private LocalDateTime updatedAt;
    private boolean franchiseeRequest;
    private UserSummaryDto franchisee;
} 