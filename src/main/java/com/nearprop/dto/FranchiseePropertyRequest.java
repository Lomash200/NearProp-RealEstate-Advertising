package com.nearprop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for franchisee adding property on behalf of another user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseePropertyRequest {
    
    @NotBlank(message = "Owner's permanent ID is required")
    private String ownerPermanentId;
    
    @NotNull(message = "Property details are required")
    private CreatePropertyDto propertyDetails;
} 