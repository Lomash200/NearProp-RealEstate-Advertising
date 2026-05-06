package com.nearprop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPropertyUpdateRequestDto {
    
    @NotNull(message = "Request ID is required")
    private Long requestId;
    
    @NotNull(message = "Approval status is required")
    private Boolean approved;
    
    @NotNull(message = "Reviewer type is required")
    private ReviewerType reviewerType;
    
    private String notes;
    
    // Required only if rejected
    private String rejectionReason;
    
    public enum ReviewerType {
        ADMIN,
        FRANCHISEE,
        SUBADMIN
    }
} 