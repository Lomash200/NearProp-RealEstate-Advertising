package com.nearprop.dto.franchisee;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DistrictDto {
    
    private Long id;
    
    @NotBlank(message = "District name is required")
    private String name;
    
    @NotBlank(message = "State is required")
    private String state;
    
    @NotBlank(message = "City is required")
    private String city;
    
    private String pincode;
    
    @NotNull(message = "Revenue share percentage is required")
    @DecimalMin(value = "0.0", message = "Revenue share percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Revenue share percentage cannot exceed 100")
    private BigDecimal revenueSharePercentage;
    
    // Geographical coordinates for the district center
    private BigDecimal latitude;
    private BigDecimal longitude;
    
    // Radius of the district in kilometers
    private Double radiusKm;
    
    @NotNull(message = "Active status is required")
    private Boolean active;
    
    // Statistics (not part of entity, but useful in responses)
    private Long propertyCount;
    private Boolean hasFranchisee;
    private String franchiseeName;
    private Long franchiseeId;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 