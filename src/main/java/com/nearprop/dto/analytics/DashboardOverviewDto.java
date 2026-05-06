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
public class DashboardOverviewDto {

    // User's role for this dashboard view
    private String role;
    
    // Basic stats
    private int totalProperties;
    private int activeProperties;
    private Integer soldProperties;
    private Integer expiringSoonProperties; // <-- Add expiring properties
    private Integer totalReels;
    private Integer totalFavorites;
    
    // Performance metrics
    private Long totalViews; // <-- For views per property
    private Integer totalInquiries; // <-- Total inquiries
    private Integer totalScheduledVisits; // <-- Total scheduled visits
    
    // Subscription info
    private Integer activeSubscriptions;
    private Integer expiringSubscriptionsNext30Days;
    private BigDecimal currentMonthSpending;
    
    // Revenue info (for admin and franchisee)
    private BigDecimal totalRevenue;
    private BigDecimal currentMonthRevenue;
    private Double revenueGrowthPercentage;
    
    // Quick charts for dashboard
    private Map<String, Integer> viewsPerDay;
    private Map<String, Integer> propertiesByStatus;
    private Map<String, Integer> visitsByStatus;
    
    // Latest activity
    private List<RecentActivityDto> recentActivities;
    
    // User growth (admin only)
    private Map<String, Integer> userRegistrationsByDay;
    private Integer newUsersToday;
    private Integer newUsersThisWeek;
    private Integer newUsersThisMonth;
    
    // System health (admin only)
    private Double systemLoad;
    private Long totalStorageUsedMB;
    private Integer activeUsers24h;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivityDto {
        private String type; // PROPERTY_VIEW, NEW_FAVORITE, NEW_VISIT, etc.
        private String description;
        private String timestamp;
        private Long userId;
        private String userName;
        private Long propertyId;
        private String propertyTitle;
    }
}
