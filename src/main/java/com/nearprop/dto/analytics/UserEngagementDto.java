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
public class UserEngagementDto {
    
    // Overall engagement
    private Integer totalViews;
    private Integer totalLikes;
    private Integer totalComments;
    private Integer totalShares;
    private Integer totalFavorites;
    
    // Content engagement
    private Map<String, Integer> viewsByContentType; // properties, reels, etc.
    private Map<String, Integer> likesByContentType;
    private Map<String, Integer> commentsByContentType;
    private Map<String, Integer> sharesByContentType;
    
    // Time trends
    private Map<String, Integer> engagementByDay;
    private Map<String, Integer> engagementByHour;
    private String mostActiveDay;
    private String mostActiveTimeOfDay;
    
    // User interaction
    private Integer totalChats;
    private Integer totalChatMessages;
    private Double averageResponseTime; // in minutes
    private Double chatCompletionRate; // percentage of chats that lead to visits/offers
    
    // Follower metrics
    private Integer totalFollowers;
    private Integer newFollowersThisMonth;
    private Double followerGrowthRate; // percentage
    private Integer totalFollowing;
    
    // Visit metrics
    private Integer totalVisitRequests;
    private Integer completedVisits;
    private Double visitCompletionRate; // percentage
    
    // Engagement quality
    private Double averageSessionDuration; // in minutes
    private Double bounceRate; // percentage of single-page sessions
    private Integer averagePageViewsPerSession;
    
    // Top performing content
    private List<TopContentDto> topViewedContent;
    private List<TopContentDto> topLikedContent;
    private List<TopContentDto> topSharedContent;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopContentDto {
        private String contentType; // property, reel, etc.
        private Long contentId;
        private String title;
        private Integer views;
        private Integer likes;
        private Integer comments;
        private Integer shares;
        private Double engagementRate;
        private String thumbnailUrl;
    }
} 