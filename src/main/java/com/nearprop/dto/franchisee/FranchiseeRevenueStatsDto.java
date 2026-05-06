package com.nearprop.dto.franchisee;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FranchiseeRevenueStatsDto {
    
    private Long franchiseeUserId;
    
    
    private String franchiseeName;
    
    private Long districtId;
    
    private String districtName;
    
    private BigDecimal totalRevenue;
    
    private BigDecimal totalCommission;
    
    private BigDecimal pendingCommission;
    
    private BigDecimal paidCommission;
    
    private Integer totalProperties;
    
    private Integer totalSubscriptions;
    
    private Integer totalTransactions;
    
    private Map<String, BigDecimal> revenueByType;
    
    private Map<String, BigDecimal> revenueByMonth;
    
    private BigDecimal monthlyGrowth;
    
    private LocalDateTime lastTransactionDate;
    
    private BigDecimal averageMonthlyRevenue;
} 