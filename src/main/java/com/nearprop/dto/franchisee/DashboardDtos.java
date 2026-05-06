package com.nearprop.dto.franchisee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Container class for dashboard-related DTOs
 */
public class DashboardDtos {

    /**
     * DTO for time series data used in graphs and charts
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSeriesData {
        private String timeUnit; // "hourly", "daily", "weekly", "monthly", "yearly"
        private List<String> labels; // Time labels (e.g., dates, hours)
        private Map<String, List<Object>> series; // Series name -> data points
    }

    /**
     * DTO for user activity data
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserActivityData {
        private Long userId;
        private String userName;
        private String action; // "viewed", "contacted", "subscribed", etc.
        private LocalDateTime timestamp;
        private String propertyId;
        private String propertyTitle;
    }

    /**
     * DTO for revenue data
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueData {
        private BigDecimal totalRevenue;
        private BigDecimal franchiseeRevenue;
        private BigDecimal platformRevenue;
        private BigDecimal walletBalance;
        private BigDecimal pendingWithdrawal;
        private LocalDate lastWithdrawalDate;
        private BigDecimal lastWithdrawalAmount;
    }

    /**
     * DTO for listing data
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListingData {
        private int totalListings;
        private int activeListings;
        private int soldListings;
        private int pendingListings;
        private int featuredListings;
        private int premiumListings;
        private int newListingsThisMonth;
        private int soldListingsThisMonth;
    }

    /**
     * DTO for subscription data
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionData {
        private int totalSubscriptions;
        private int activeSubscriptions;
        private int expiredSubscriptions;
        private int newSubscriptionsThisMonth;
        private int renewalsThisMonth;
        private BigDecimal averageSubscriptionValue;
        private BigDecimal totalSubscriptionRevenue;
    }

    /**
     * DTO for user registration stats (daily, weekly, monthly)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRegistrationStats {
        private List<String> dailyLabels;
        private List<Integer> dailyCounts;
        private List<String> weeklyLabels;
        private List<Integer> weeklyCounts;
        private List<String> monthlyLabels;
        private List<Integer> monthlyCounts;
    }
} 