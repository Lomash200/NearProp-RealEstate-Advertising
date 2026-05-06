package com.nearprop.dto.franchisee;

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
public class FranchiseeDistrictRevenueDto {
    private Long districtId;
    private String districtName;
    private String state;
    
    // Revenue totals
    private BigDecimal totalRevenue;
    private BigDecimal totalCommission;
    private BigDecimal withdrawnAmount;
    
    // Transaction counts
    private Integer totalTransactions;
    private Integer completedTransactions;
    
    // Subscription stats
    private Integer totalSubscribers;
    private Integer activeSubscribers;
    
    // Time period
    private LocalDateTime startDate;
    private LocalDateTime lastTransactionDate;
}
