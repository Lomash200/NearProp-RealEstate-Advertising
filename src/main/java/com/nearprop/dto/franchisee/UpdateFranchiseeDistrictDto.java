package com.nearprop.dto.franchisee;

import com.nearprop.entity.FranchiseeDistrict.FranchiseeStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for updating an existing franchisee district
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFranchiseeDistrictDto {
    
    @NotNull(message = "Franchisee user ID is required")
    private Long franchiseeUserId;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private Boolean active;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Revenue share percentage must be positive")
    private BigDecimal revenueSharePercentage;
    
    private FranchiseeStatus status;
    
    private String officeAddress;
    
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Contact phone must be a valid phone number")
    private String contactPhone;
    
    @Email(message = "Contact email must be a valid email address")
    private String contactEmail;
    
    private String terminationReason;
} 