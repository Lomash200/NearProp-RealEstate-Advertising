package com.nearprop.dto.analytics;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReelAnalyticsDto {
    
    // Reel identification
    private Long reelId;
    private String title;
    private Long propertyId;
    private String propertyTitle;
    
    // Performance metrics
    private Integer views;
    private Integer likes;
    private Integer comments;
    private Integer shares;
    private Double engagementRate; // (likes + comments + shares) / views
    
    // Audience metrics
    private Integer uniqueViewers;
    private Map<String, Integer> viewersByCity;
    private Map<String, Double> viewersByAgeGroup;
    private Map<String, Double> viewersByGender;
    
    // Time trends
    private Map<String, Integer> viewsByDay;
    private Map<String, Integer> likesByDay;
    private Map<String, Integer> commentsByDay;
    private Map<String, Integer> sharesByDay;
    
    // Engagement metrics
    private Double averageWatchTime; // in seconds
    private Double completionRate; // percentage of viewers who watched to the end
    private Map<String, Double> dropOffPoints; // at what points in the video users stop watching
    
    // Comparative metrics
    private Double viewsComparedToUserAverage; // percentage
    private Double likesComparedToUserAverage; // percentage
    private Integer rankAmongUserReels; // rank among all reels of the user
    
    // For multiple reels analytics
    private Integer totalReels;
    private List<ReelSummary> topPerformingReels;
    private List<ReelSummary> worstPerformingReels;
    
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
        private Double engagementRate;
        private String thumbnailUrl;
    }
} 