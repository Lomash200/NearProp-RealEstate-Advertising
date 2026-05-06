package com.nearprop.dto.franchisee;

import com.nearprop.entity.FranchiseeDistrict.FranchiseeStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
public class CreateFranchiseeDistrictDto {
    
    @NotNull(message = "Franchisee user ID is required")
    private Long franchiseeUserId;
    
    @NotNull(message = "District ID is required")
    private Long districtId;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    @NotNull(message = "Revenue share percentage is required")
    @DecimalMin(value = "0.0", message = "Revenue share percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Revenue share percentage cannot exceed 100")
    private BigDecimal revenueSharePercentage;
    
    private String officeAddress;
    
    private String contactPhone;
    
    private String contactEmail;
    
    @Builder.Default
    private FranchiseeStatus status = FranchiseeStatus.ACTIVE;
    
    @Builder.Default
    private Boolean active = true;
} 