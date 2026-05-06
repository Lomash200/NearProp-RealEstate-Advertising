package com.nearprop.dto.analytics;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionAnalyticsDto {
    
    // Subscription counts
    private Integer totalSubscriptions;
    private Integer activeSubscriptions;
    private Integer expiredSubscriptions;
    private Integer cancelledSubscriptions;
    private Integer autoRenewSubscriptions;
    
    // Subscription types
    private Map<String, Integer> subscriptionsByPlanType;
    private String mostPopularPlan;
    
    // Financial metrics
    private BigDecimal totalSpent;
    private BigDecimal currentMonthSpending;
    private BigDecimal averageMonthlySpending;
    private BigDecimal projectedAnnualSpending;
    
    // Time trends
    private Map<String, Integer> subscriptionsByMonth;
    private Map<String, BigDecimal> spendingByMonth;
    
    // Subscription behavior
    private Double renewalRate; // percentage of subscriptions that get renewed
    private Double averageSubscriptionDuration; // in months
    private Double churnRate; // percentage of subscriptions that are not renewed
    
    // For admin/franchisee
    private BigDecimal totalRevenue;
    private BigDecimal currentMonthRevenue;
    private Double revenueGrowthPercentage;
    private Map<String, BigDecimal> revenueByPlanType;
    
    // Upcoming events
    private Integer expiringNext7Days;
    private Integer expiringNext30Days;
    private BigDecimal projectedRenewalRevenue;
    
    // Subscription details
    private List<SubscriptionDetail> activeSubscriptionDetails;
    private List<SubscriptionDetail> expiringSubscriptionDetails;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionDetail {
        private Long subscriptionId;
        private String planName;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal price;
        private Boolean autoRenew;
        private Long propertyId;
        private String propertyTitle;
    }
} 