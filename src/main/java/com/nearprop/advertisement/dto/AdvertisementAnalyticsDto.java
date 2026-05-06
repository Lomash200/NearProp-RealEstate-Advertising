package com.nearprop.advertisement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementAnalyticsDto {
    private Long advertisementId;
    private String title;
    private String districtName;
    
    // Total counts
    private Long viewCount;
    private Long clickCount;
    private Long whatsappClicks;
    private Long phoneClicks;
    private Long websiteClicks;
    private Long socialMediaClicks;
    
    // Click breakdown by social media
    private Long instagramClicks;
    private Long facebookClicks;
    private Long youtubeClicks;
    private Long twitterClicks;
    private Long linkedinClicks;
    
    // Time-based analytics
    @Builder.Default
    private List<DailyAnalytics> dailyAnalytics = new ArrayList<>();
    
    // District-based analytics
    @Builder.Default
    private Map<String, Long> clicksByDistrict = new HashMap<>();
    
    // User demographics (if available)
    private Long uniqueUsers;
    private Long loggedInUsers;
    private Long anonymousUsers;
    
    // Conversion metrics
    private Double clickThroughRate; // clicks / views
    private Double conversionRate; // actions / clicks
    
    // Advertisement status
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private boolean active;
    private boolean expired;
    private Long daysRemaining;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyAnalytics {
        private LocalDateTime date;
        private Long views;
        private Long clicks;
        private Map<String, Long> clicksByType;
    }
} 