package com.nearprop.dto.franchisee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for district performance metrics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistrictPerformanceDto {
    private Long districtId;
    private String districtName;
    private String state;
    
    // Listing statistics
    private int totalListings;
    private int activeListings;
    private int soldListings;
    private int pendingListings;
    private int featuredListings;
    
    // Revenue statistics
    private BigDecimal totalRevenue;
    private BigDecimal franchiseeRevenue;
    private BigDecimal platformRevenue;
    
    // Subscription statistics
    private int totalSubscriptions;
    private int activeSubscriptions;
    private int expiredSubscriptions;
    
    // Time series data for graphs
    private List<DashboardDtos.TimeSeriesData> listingTimeSeriesData;
    private List<DashboardDtos.TimeSeriesData> revenueTimeSeriesData;
    private List<DashboardDtos.TimeSeriesData> subscriptionTimeSeriesData;
    
    // User engagement metrics
    private int totalUsers;
    private int activeUsers;
    private int newUsersThisMonth;
    
    // Performance ranking (compared to other districts)
    private int revenueRank;
    private int listingsRank;
    private int subscriptionsRank;
    
    // Growth metrics (percentage change from previous period)
    private double revenueGrowth;
    private double listingsGrowth;
    private double subscriptionsGrowth;
} 