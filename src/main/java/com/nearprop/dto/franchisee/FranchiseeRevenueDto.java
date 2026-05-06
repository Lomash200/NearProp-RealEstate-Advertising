package com.nearprop.dto.franchisee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseeRevenueDto {
    
    // District details
    private Long districtId;
    private String districtName;
    private String state;
    
    // Franchisee details
    private Long franchiseeId;
    private String franchiseeName;
    private String businessName;
    
    // Revenue summary
    private BigDecimal totalRevenue;
    private BigDecimal franchiseeCommission;
    private BigDecimal platformCommission;
    
    // Revenue period
    private LocalDate startDate;
    private LocalDate endDate;
    
    // Transaction details
    private Integer totalTransactions;
    private Integer totalProperties;
    
    // Monthly breakdown
    private List<MonthlyRevenue> monthlyBreakdown;
    
    // Revenue by property type
    private Map<String, BigDecimal> revenueByPropertyType;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyRevenue {
        private Integer year;
        private Integer month;
        private BigDecimal revenue;
        private BigDecimal commission;
        private Integer transactions;
    }
} 