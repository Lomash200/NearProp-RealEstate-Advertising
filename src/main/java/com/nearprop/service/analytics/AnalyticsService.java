package com.nearprop.service.analytics;

import com.nearprop.dto.analytics.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Service for generating analytics and insights across the platform
 */
public interface AnalyticsService {
    
    /**
     * Get dashboard overview statistics for a specific role
     * 
     * @param userId User ID
     * @param role Role name (ADMIN, SELLER, ADVISOR, etc.)
     * @return Dashboard overview metrics
     */
    DashboardOverviewDto getDashboardOverview(Long userId, String role);
    
    /**
     * Get property analytics for a specific property or all properties owned by a user
     * 
     * @param userId User ID
     * @param propertyId Optional property ID (null for all user properties)
     * @param startDate Start date for analytics period
     * @param endDate End date for analytics period
     * @return Property analytics data
     */
    PropertyAnalyticsDto getPropertyAnalytics(Long userId, Long propertyId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get reel performance analytics
     * 
     * @param userId User ID
     * @param reelId Optional reel ID (null for all user reels)
     * @param startDate Start date for analytics period
     * @param endDate End date for analytics period
     * @return Reel performance analytics
     */
    ReelAnalyticsDto getReelAnalytics(Long userId, Long reelId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get visit analytics for properties
     * 
     * @param userId User ID
     * @param propertyId Optional property ID (null for all user properties)
     * @param startDate Start date for analytics period
     * @param endDate End date for analytics period
     * @return Visit analytics data
     */
    VisitAnalyticsDto getVisitAnalytics(Long userId, Long propertyId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get subscription analytics
     * 
     * @param userId User ID
     * @param startDate Start date for analytics period
     * @param endDate End date for analytics period
     * @return Subscription analytics data
     */
    SubscriptionAnalyticsDto getSubscriptionAnalytics(Long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get revenue analytics (admin and franchisee roles)
     * 
     * @param userId User ID
     * @param districtId Optional district ID (for franchisees)
     * @param startDate Start date for analytics period
     * @param endDate End date for analytics period
     * @return Revenue analytics data
     */
    RevenueAnalyticsDto getRevenueAnalytics(Long userId, Long districtId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get user engagement metrics
     * 
     * @param userId User ID
     * @param startDate Start date for analytics period
     * @param endDate End date for analytics period
     * @return User engagement metrics
     */
    UserEngagementDto getUserEngagementAnalytics(Long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get system-wide analytics (admin only)
     * 
     * @param startDate Start date for analytics period
     * @param endDate End date for analytics period
     * @return System overview analytics
     */
    SystemAnalyticsDto getSystemAnalytics(LocalDate startDate, LocalDate endDate);
    
    /**
     * Export analytics data in specified format
     * 
     * @param userId User ID
     * @param analyticsType Type of analytics to export
     * @param format Export format (CSV, PDF, EXCEL)
     * @param startDate Start date for analytics period
     * @param endDate End date for analytics period
     * @return URL to download the exported file
     */
    String exportAnalytics(Long userId, String analyticsType, String format, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get performance trends over time
     * 
     * @param userId User ID
     * @param metricType The type of metric to track (VIEWS, VISITS, REVENUE, etc.)
     * @param aggregation Aggregation level (DAY, WEEK, MONTH)
     * @param startDate Start date for analytics period
     * @param endDate End date for analytics period
     * @return Mapping of dates to metric values
     */
    Map<String, Double> getPerformanceTrends(Long userId, String metricType, String aggregation, 
                                           LocalDate startDate, LocalDate endDate);
} 