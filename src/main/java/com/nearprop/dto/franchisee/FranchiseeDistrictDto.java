package com.nearprop.dto.franchisee;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nearprop.dto.UserSummaryDto;
import com.nearprop.entity.FranchiseeDistrict.FranchiseeStatus;
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
public class FranchiseeDistrictDto {
    
    private Long id;
    
    private UserSummaryDto franchisee;
    
    private Long districtId;
    
    private String districtName;
    
    private String state;
    
    private FranchiseRequestDto franchiseRequest;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private Boolean active;
    
    private BigDecimal revenueSharePercentage;
    
    private Integer totalProperties;
    
    private Integer totalTransactions;
    
    private BigDecimal totalRevenue;
    
    private BigDecimal totalCommission;
    
    private FranchiseeStatus status;
    
    private String officeAddress;
    
    private String contactPhone;
    
    private String contactEmail;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 