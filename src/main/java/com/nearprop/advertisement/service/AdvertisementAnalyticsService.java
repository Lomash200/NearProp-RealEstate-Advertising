package com.nearprop.advertisement.service;

import com.nearprop.advertisement.dto.AdvertisementAnalyticsDto;
import com.nearprop.advertisement.entity.AdvertisementClick;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AdvertisementAnalyticsService {
    
    /**
     * Record a view event for an advertisement
     * @param advertisementId Advertisement ID
     * @param userId User ID (optional)
     * @param ipAddress IP address
     * @param userAgent User agent
     * @param userDistrict User district (optional)
     * @param referrer Referrer URL (optional)
     */
    void recordView(Long advertisementId, Long userId, String ipAddress, String userAgent, String userDistrict, String referrer);
    
    /**
     * Record a click event for an advertisement
     * @param advertisementId Advertisement ID
     * @param clickType Type of click
     * @param userId User ID (optional)
     * @param ipAddress IP address
     * @param userAgent User agent
     * @param userDistrict User district (optional)
     * @param referrer Referrer URL (optional)
     */
    void recordClick(Long advertisementId, AdvertisementClick.ClickType clickType, Long userId, String ipAddress, String userAgent, String userDistrict, String referrer);
    
    /**
     * Get analytics for a specific advertisement
     * @param advertisementId Advertisement ID
     * @return Advertisement analytics
     */
    AdvertisementAnalyticsDto getAdvertisementAnalytics(Long advertisementId);
    
    /**
     * Get analytics for all advertisements
     * @param pageable Pagination information
     * @return Page of advertisement analytics
     */
    Page<AdvertisementAnalyticsDto> getAllAdvertisementsAnalytics(Pageable pageable);
    
    /**
     * Get analytics for all advertisements by a specific advertiser
     * @param userId User ID of the advertiser
     * @return List of advertisement analytics
     */
    List<AdvertisementAnalyticsDto> getAdvertiserAnalytics(Long userId);
    
    /**
     * Get analytics for all advertisements in a specific district
     * @param districtName District name
     * @return List of advertisement analytics
     */
    List<AdvertisementAnalyticsDto> getDistrictAdvertisementsAnalytics(String districtName);
    
    /**
     * Get analytics for social media clicks
     * @return Map of social media platform to click count
     */
    Map<String, Long> getSocialMediaClicksAnalytics();
    
    /**
     * Get analytics for a specific advertisement within a date range
     * @param advertisementId Advertisement ID
     * @param startDate Start date
     * @param endDate End date
     * @return Advertisement analytics for the date range
     */
    AdvertisementAnalyticsDto getAdvertisementAnalyticsForDateRange(Long advertisementId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get overall analytics summary for all advertisements
     * @return Map of analytics metrics
     */
    Map<String, Object> getOverallAnalyticsSummary();
} 