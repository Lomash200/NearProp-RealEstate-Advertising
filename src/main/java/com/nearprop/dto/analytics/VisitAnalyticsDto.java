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
public class VisitAnalyticsDto {
    
    // Visit counts
    private Integer totalVisits;
    private Integer pendingVisits;
    private Integer completedVisits;
    private Integer cancelledVisits;
    private Integer rescheduledVisits;
    
    // Time trends
    private Map<String, Integer> visitsByDay;
    private Map<String, Integer> visitsByMonth;
    private Map<String, Integer> visitsByStatus;
    
    // Visit timing
    private Map<String, Integer> visitsByTimeOfDay; // morning, afternoon, evening
    private Map<String, Integer> visitsByDayOfWeek; // Monday through Sunday
    private String mostPopularDay;
    private String mostPopularTimeSlot;
    
    // Visit conversion
    private Double visitToOfferRate; // percentage of visits that result in offers
    private Double visitToSaleRate; // percentage of visits that result in sales/rentals
    private Double cancelledVisitRate; // percentage of scheduled visits that get cancelled
    
    // Property specific (when propertyId is provided)
    private Long propertyId;
    private String propertyTitle;
    private Double averageVisitDuration; // in minutes
    private Double averageTimeToSchedule; // days between viewing listing and scheduling visit
    
    // Visitor demographics
    private Map<String, Integer> visitorsByAgeGroup;
    private Map<String, Integer> visitorsByBudgetRange;
    private Map<String, Integer> visitorsByRequirements; // 1BHK, 2BHK, etc.
    
    // For multiple properties
    private List<PropertyVisitSummary> topVisitedProperties;
    private List<PropertyVisitSummary> propertiesWithHighestConversion;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropertyVisitSummary {
        private Long propertyId;
        private String title;
        private Integer totalVisits;
        private Integer completedVisits;
        private Double conversionRate;
        private String thumbnailUrl;
    }
} 