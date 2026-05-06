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
public class SystemAnalyticsDto {
    
    // User metrics
    private Integer totalUsers;
    private Integer activeUsers;
    private Integer newUsersThisMonth;
    private Double userGrowthRate; // percentage
    private Map<String, Integer> usersByRole;
    private Map<String, Integer> usersByCity;
    private Map<String, Integer> userRegistrationsByDay;
    
    // Content metrics
    private Integer totalProperties;
    private Integer totalReels;
    private Integer newPropertiesThisMonth;
    private Integer newReelsThisMonth;
    private Map<String, Integer> propertiesByStatus;
    private Map<String, Integer> propertiesByType;
    private Map<String, Integer> propertiesByCity;
    
    // Engagement metrics
    private Long totalPageViews;
    private Long totalApiCalls;
    private Integer totalVisits;
    private Integer totalFavorites;
    private Map<String, Long> pageViewsBySection;
    private Map<String, Long> apiCallsByEndpoint;
    
    // Subscription metrics
    private Integer totalSubscriptions;
    private Integer activeSubscriptions;
    private Integer newSubscriptionsThisMonth;
    private Double subscriptionGrowthRate; // percentage
    private Map<String, Integer> subscriptionsByPlanType;
    
    // Revenue metrics
    private BigDecimal totalRevenue;
    private BigDecimal currentMonthRevenue;
    private BigDecimal previousMonthRevenue;
    private Double revenueGrowthRate; // percentage
    private Map<String, BigDecimal> revenueByMonth;
    
    // System performance
    private Double averageApiResponseTime; // in milliseconds
    private Double systemLoad;
    private Long totalStorageUsed; // in MB
    private Integer activeConnections;
    private Map<String, Double> errorRateByEndpoint; // percentage
    
    // Top performers
    private List<TopPerformerDto> topPerformingCities;
    private List<TopPerformerDto> topPerformingDistricts;
    private List<TopPerformerDto> topPerformingFranchisees;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopPerformerDto {
        private String name;
        private Integer userCount;
        private Integer propertyCount;
        private BigDecimal revenue;
        private Double growthRate;
    }
} 