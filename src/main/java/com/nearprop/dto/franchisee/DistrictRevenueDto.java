package com.nearprop.dto.franchisee;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nearprop.dto.UserSummaryDto;
import com.nearprop.entity.DistrictRevenue.PaymentStatus;
import com.nearprop.entity.DistrictRevenue.RevenueType;
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
public class DistrictRevenueDto {
    
    private Long id;
    
    private Long districtId;
    
    private String districtName;
    
    private String state;
    
    private FranchiseeDistrictDto franchiseeDistrict;
    
    private UserSummaryDto franchisee;
    
    private RevenueType revenueType;
    
    private Long propertyId;
    
    private Long subscriptionId;
    
    private String transactionId;
    
    private LocalDateTime transactionDate;
    
    private BigDecimal amount;
    
    private BigDecimal franchiseeCommission;
    
    private BigDecimal companyRevenue;
    
    private PaymentStatus paymentStatus;
    
    private LocalDateTime paymentDate;
    
    private String paymentReference;
    
    private String description;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 