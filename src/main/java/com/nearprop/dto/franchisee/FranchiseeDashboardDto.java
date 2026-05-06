package com.nearprop.dto.franchisee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Main DTO for franchisee dashboard data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseeDashboardDto {
    // Metadata
    private Long franchiseeId;
    private String franchiseeName;
    private String businessName;
    private LocalDateTime generatedAt;
    private List<Long> districtIds;
    
    // Summary statistics
    private int totalListings;
    private int activeListings;
    private int soldListings;
    private int pendingListings;
    
    // Revenue information
    private BigDecimal totalRevenue;
    private BigDecimal franchiseeRevenue;
    private BigDecimal platformRevenue;
    private BigDecimal walletBalance;
    private BigDecimal pendingWithdrawal;
    
    // Subscription information
    private int totalSubscriptions;
    private int activeSubscriptions;
    private int expiredSubscriptions;
    
    // Time series data for graphs
    private List<DashboardDtos.TimeSeriesData> listingTimeSeriesData;
    private List<DashboardDtos.TimeSeriesData> subscriptionTimeSeriesData;
    
    // New: User registration stats (daily, weekly, monthly)
    private DashboardDtos.UserRegistrationStats userRegistrationStats;
    
    // District performance
    private List<DistrictPerformanceDto> districtPerformance;
    
    // Active subscription users
    private List<SubscriptionUserDto> activeSubscriptionUsers;
    
    // Additional metrics
    private DashboardDtos.ListingData listingData;
    private DashboardDtos.RevenueData revenueData;
    private DashboardDtos.SubscriptionData subscriptionData;
} 