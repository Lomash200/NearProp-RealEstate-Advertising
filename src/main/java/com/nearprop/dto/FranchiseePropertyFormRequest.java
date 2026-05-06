package com.nearprop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for franchisee adding property via form on behalf of another user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseePropertyFormRequest {
    
    @NotBlank(message = "Owner's permanent ID is required")
    private String ownerPermanentId;
    
    @NotNull(message = "Property form details are required")
    private PropertyFormDto propertyFormDetails;
} 