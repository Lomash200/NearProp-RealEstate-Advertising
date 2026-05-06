package com.nearprop.dto.franchisee;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDistrictDto {
    
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
    
    private BigDecimal latitude;
    
    private BigDecimal longitude;
    
    private Double radiusKm;
    
    @Builder.Default
    private Boolean active = true;
} 