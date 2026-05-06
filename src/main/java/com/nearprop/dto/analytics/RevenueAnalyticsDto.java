package com.nearprop.dto.analytics;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RevenueAnalyticsDto {
    
    // Overall revenue
    private BigDecimal totalRevenue;
    private BigDecimal currentMonthRevenue;
    private BigDecimal previousMonthRevenue;
    private Double monthOverMonthGrowth; // percentage
    private BigDecimal projectedAnnualRevenue;
    
    // Revenue breakdown
    private Map<String, BigDecimal> revenueByPlanType;
    private Map<String, BigDecimal> revenueByMonth;
    private Map<String, BigDecimal> revenueByDistrict;
    private Map<String, BigDecimal> revenueByCity;
    
    // Transaction metrics
    private Integer totalTransactions;
    private BigDecimal averageTransactionValue;
    private Integer newSubscriptions;
    private Integer renewals;
    private Double renewalRate; // percentage
    
    // District specific (for franchisees)
    private Long districtId;
    private String districtName;
    private BigDecimal districtTotalRevenue;
    private BigDecimal districtCurrentMonthRevenue;
    private Double districtMonthOverMonthGrowth;
    
    // Marketing fee metrics (for franchisees)
    private BigDecimal totalMarketingFees;
    private BigDecimal currentMonthMarketingFees;
    private Double marketingFeePercentage; // of total revenue
    
    // Top performers
    private List<TopPerformerDto> topPerformingDistricts;
    private List<TopPerformerDto> topPerformingCities;
    private List<TopPerformerDto> topPerformingPlans;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopPerformerDto {
        private String name;
        private BigDecimal revenue;
        private Integer transactions;
        private Double growthPercentage;
    }
} 