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
public class PropertyAnalyticsDto {
    
    // Property identification
    private Long propertyId;
    private String propertyTitle;
    private String propertyStatus;
    private String district;
    private String city;
    
    // Engagement metrics
    private Integer totalViews;
    private Integer uniqueVisitors;
    private Integer viewsLast7Days;
    private Integer viewsLast30Days;
    private Double viewsPerDayAverage;
    private Integer totalFavorites;
    private Integer favoritesLast7Days;
    
    // Visit metrics
    private Integer totalVisits;
    private Integer pendingVisits;
    private Integer completedVisits;
    private Integer cancelledVisits;
    private Double visitsPerWeekAverage;
    
    // Time trends
    private Map<String, Integer> viewsByDay;
    private Map<String, Integer> favoritesByDay;
    private Map<String, Integer> visitsByDay;
    
    // Engagement duration
    private Double averageTimeOnPage; // in seconds
    
    // Comparative metrics
    private Integer rankInDistrict; // Rank among properties in the same district
    private Integer rankInCity; // Rank among properties in the same city
    private Double viewsComparedToSimilarProperties; // Percentage
    
    // Reel performance summary
    private Integer totalReels;
    private Integer totalReelViews;
    private Integer totalReelLikes;
    private Integer totalReelShares;
    private List<ReelSummary> topPerformingReels;
    
    // Subscription metrics
    private Boolean activeSubscription;
    private String subscriptionPlan;
    private Integer daysUntilExpiry;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReelSummary {
        private Long reelId;
        private String title;
        private Integer views;
        private Integer likes;
        private Integer comments;
        private Integer shares;
        private Double engagementRate; // (likes + comments + shares) / views
    }
} 