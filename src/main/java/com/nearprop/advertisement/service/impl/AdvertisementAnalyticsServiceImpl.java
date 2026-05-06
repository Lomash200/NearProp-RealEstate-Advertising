package com.nearprop.advertisement.service.impl;

import com.nearprop.advertisement.dto.AdvertisementAnalyticsDto;
import com.nearprop.advertisement.entity.Advertisement;
import com.nearprop.advertisement.entity.AdvertisementClick;
import com.nearprop.advertisement.entity.AdvertisementClick.ClickType;
import com.nearprop.advertisement.repository.AdvertisementClickRepository;
import com.nearprop.advertisement.repository.AdvertisementRepository;
import com.nearprop.advertisement.service.AdvertisementAnalyticsService;
import com.nearprop.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvertisementAnalyticsServiceImpl implements AdvertisementAnalyticsService {

    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementClickRepository advertisementClickRepository;

    @Override
    @Transactional
    @Async
    public void recordView(Long advertisementId, Long userId, String ipAddress, String userAgent, String userDistrict, String referrer) {
        log.debug("Recording view for advertisement ID: {}, user ID: {}", advertisementId, userId);
        
        try {
            Advertisement advertisement = advertisementRepository.findById(advertisementId)
                    .orElseThrow(() -> new EntityNotFoundException("Advertisement not found with ID: " + advertisementId));
            
            // Create click event
            AdvertisementClick click = new AdvertisementClick();
            click.setAdvertisement(advertisement);
            click.setClickType(ClickType.VIEW);
            click.setIpAddress(ipAddress);
            click.setUserAgent(userAgent);
            click.setUserId(userId);
            click.setUserDistrict(userDistrict);
            click.setReferrer(referrer);
            
            advertisementClickRepository.save(click);
            
            // Update view count on advertisement
            advertisement.setViewCount(advertisement.getViewCount() + 1);
            advertisementRepository.save(advertisement);
            
            log.debug("View recorded for advertisement ID: {}, new view count: {}", advertisementId, advertisement.getViewCount());
        } catch (Exception e) {
            log.error("Error recording view for advertisement ID: {}", advertisementId, e);
            // Don't rethrow - we don't want to interrupt the user experience
        }
    }

    @Override
    @Transactional
    @Async
    public void recordClick(Long advertisementId, ClickType clickType, Long userId, String ipAddress, String userAgent, String userDistrict, String referrer) {
        log.debug("Recording click type: {} for advertisement ID: {}, user ID: {}", clickType, advertisementId, userId);
        
        try {
            Advertisement advertisement = advertisementRepository.findById(advertisementId)
                    .orElseThrow(() -> new EntityNotFoundException("Advertisement not found with ID: " + advertisementId));
            
            // Create click event
            AdvertisementClick click = new AdvertisementClick();
            click.setAdvertisement(advertisement);
            click.setClickType(clickType);
            click.setIpAddress(ipAddress);
            click.setUserAgent(userAgent);
            click.setUserId(userId);
            click.setUserDistrict(userDistrict);
            click.setReferrer(referrer);
            
            advertisementClickRepository.save(click);
            
            // Update click counts on advertisement
            advertisement.setClickCount(advertisement.getClickCount() + 1);
            
            // Update specific click type count
            switch (clickType) {
                case WHATSAPP:
                    advertisement.setWhatsappClicks(advertisement.getWhatsappClicks() + 1);
                    break;
                case PHONE:
                    advertisement.setPhoneClicks(advertisement.getPhoneClicks() + 1);
                    break;
                case WEBSITE:
                    advertisement.setWebsiteClicks(advertisement.getWebsiteClicks() + 1);
                    break;
                case INSTAGRAM:
                case FACEBOOK:
                case YOUTUBE:
                case TWITTER:
                case LINKEDIN:
                    advertisement.setSocialMediaClicks(advertisement.getSocialMediaClicks() + 1);
                    break;
                default:
                    // No specific counter for other types
                    break;
            }
            
            advertisementRepository.save(advertisement);
            
            log.debug("Click recorded for advertisement ID: {}, new click count: {}", advertisementId, advertisement.getClickCount());
        } catch (Exception e) {
            log.error("Error recording click for advertisement ID: {}", advertisementId, e);
            // Don't rethrow - we don't want to interrupt the user experience
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AdvertisementAnalyticsDto getAdvertisementAnalytics(Long advertisementId) {
        log.debug("Getting analytics for advertisement ID: {}", advertisementId);
        
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found with ID: " + advertisementId));
        
        return buildAnalyticsDto(advertisement);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<AdvertisementAnalyticsDto> getAllAdvertisementsAnalytics(Pageable pageable) {
        log.debug("Getting analytics for all advertisements, page: {}, size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Advertisement> advertisements = advertisementRepository.findAll(pageable);
        return advertisements.map(this::buildAnalyticsDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdvertisementAnalyticsDto> getAdvertiserAnalytics(Long userId) {
        log.debug("Getting analytics for advertiser ID: {}", userId);
        
        List<Advertisement> advertisements = advertisementRepository.findByCreatedById(userId);
        return advertisements.stream()
                .map(this::buildAnalyticsDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdvertisementAnalyticsDto> getDistrictAdvertisementsAnalytics(String districtName) {
        log.debug("Getting analytics for advertisements in district: {}", districtName);
        
        List<Advertisement> advertisements = advertisementRepository.findByDistrictName(districtName);
        return advertisements.stream()
                .map(this::buildAnalyticsDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getSocialMediaClicksAnalytics() {
        log.debug("Getting social media clicks analytics");
        
        Map<String, Long> socialMediaClicks = new HashMap<>();
        
        // Count clicks for each social media platform
        socialMediaClicks.put("INSTAGRAM", 
                advertisementClickRepository.countByClickType(ClickType.INSTAGRAM));
        socialMediaClicks.put("FACEBOOK", 
                advertisementClickRepository.countByClickType(ClickType.FACEBOOK));
        socialMediaClicks.put("YOUTUBE", 
                advertisementClickRepository.countByClickType(ClickType.YOUTUBE));
        socialMediaClicks.put("TWITTER", 
                advertisementClickRepository.countByClickType(ClickType.TWITTER));
        socialMediaClicks.put("LINKEDIN", 
                advertisementClickRepository.countByClickType(ClickType.LINKEDIN));
        
        return socialMediaClicks;
    }
    
    private AdvertisementAnalyticsDto buildAnalyticsDto(Advertisement advertisement) {
        AdvertisementAnalyticsDto analyticsDto = new AdvertisementAnalyticsDto();
        analyticsDto.setAdvertisementId(advertisement.getId());
        analyticsDto.setTitle(advertisement.getTitle());
        analyticsDto.setDistrictName(advertisement.getDistrictName());
        
        // Set total counts
        analyticsDto.setViewCount(advertisement.getViewCount());
        analyticsDto.setClickCount(advertisement.getClickCount());
        analyticsDto.setWhatsappClicks(advertisement.getWhatsappClicks());
        analyticsDto.setPhoneClicks(advertisement.getPhoneClicks());
        analyticsDto.setWebsiteClicks(advertisement.getWebsiteClicks());
        analyticsDto.setSocialMediaClicks(advertisement.getSocialMediaClicks());
        
        // Get social media click breakdown
        Long adId = advertisement.getId();
        analyticsDto.setInstagramClicks(advertisementClickRepository.countByAdvertisementIdAndClickType(adId, ClickType.INSTAGRAM));
        analyticsDto.setFacebookClicks(advertisementClickRepository.countByAdvertisementIdAndClickType(adId, ClickType.FACEBOOK));
        analyticsDto.setYoutubeClicks(advertisementClickRepository.countByAdvertisementIdAndClickType(adId, ClickType.YOUTUBE));
        analyticsDto.setTwitterClicks(advertisementClickRepository.countByAdvertisementIdAndClickType(adId, ClickType.TWITTER));
        analyticsDto.setLinkedinClicks(advertisementClickRepository.countByAdvertisementIdAndClickType(adId, ClickType.LINKEDIN));
        
        // Get unique users
        analyticsDto.setUniqueUsers(advertisementClickRepository.countUniqueUsersByAdvertisementId(adId));
        
        // Count logged in vs anonymous users
        analyticsDto.setLoggedInUsers(advertisementClickRepository.countLoggedInUsersByAdvertisementId(adId));
        analyticsDto.setAnonymousUsers(advertisementClickRepository.countAnonymousUsersByAdvertisementId(adId));
        
        // Calculate click through and conversion rates
        if (analyticsDto.getViewCount() > 0) {
            double ctr = (double) analyticsDto.getClickCount() / analyticsDto.getViewCount();
            analyticsDto.setClickThroughRate(Math.round(ctr * 1000.0) / 1000.0); // Round to 3 decimal places
        }
        
        // Set district-based analytics
        Map<String, Long> clicksByDistrict = new HashMap<>();
        List<Object[]> districtData = advertisementClickRepository.countClicksByDistrict(adId);
        for (Object[] data : districtData) {
            String district = (String) data[0];
            Long count = (Long) data[1];
            clicksByDistrict.put(district, count);
        }
        analyticsDto.setClicksByDistrict(clicksByDistrict);
        
        // Set daily analytics
        List<Object[]> dailyData = advertisementClickRepository.countClicksByDateAndType(adId);
        Map<LocalDate, AdvertisementAnalyticsDto.DailyAnalytics> dailyAnalyticsMap = new HashMap<>();
        
        for (Object[] data : dailyData) {
            LocalDate date = ((java.sql.Date) data[0]).toLocalDate();
            ClickType clickType = (ClickType) data[1];
            Long count = (Long) data[2];
            
            dailyAnalyticsMap.computeIfAbsent(date, k -> {
                AdvertisementAnalyticsDto.DailyAnalytics daily = new AdvertisementAnalyticsDto.DailyAnalytics();
                daily.setDate(date.atStartOfDay());
                daily.setViews(0L);
                daily.setClicks(0L);
                daily.setClicksByType(new HashMap<>());
                return daily;
            });
            
            AdvertisementAnalyticsDto.DailyAnalytics daily = dailyAnalyticsMap.get(date);
            
            if (clickType == ClickType.VIEW) {
                daily.setViews(count);
            } else {
                daily.setClicks(daily.getClicks() + count);
                daily.getClicksByType().put(clickType.name(), count);
            }
        }
        
        analyticsDto.setDailyAnalytics(new ArrayList<>(dailyAnalyticsMap.values()));
        
        // Set advertisement status
        analyticsDto.setValidFrom(advertisement.getValidFrom());
        analyticsDto.setValidUntil(advertisement.getValidUntil());
        analyticsDto.setActive(advertisement.isActive());
        analyticsDto.setExpired(LocalDateTime.now().isAfter(advertisement.getValidUntil()));
        
        if (!analyticsDto.isExpired()) {
            long daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), advertisement.getValidUntil());
            analyticsDto.setDaysRemaining(daysRemaining);
        } else {
            analyticsDto.setDaysRemaining(0L);
        }
        
        return analyticsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public AdvertisementAnalyticsDto getAdvertisementAnalyticsForDateRange(Long advertisementId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting analytics for advertisement ID: {} between {} and {}", advertisementId, startDate, endDate);
        
        // First get the standard analytics
        AdvertisementAnalyticsDto analyticsDto = getAdvertisementAnalytics(advertisementId);
        
        // Then filter the daily analytics to the specified date range
        List<AdvertisementAnalyticsDto.DailyAnalytics> filteredDailyAnalytics = analyticsDto.getDailyAnalytics().stream()
                .filter(daily -> !daily.getDate().isBefore(startDate) && !daily.getDate().isAfter(endDate))
                .collect(Collectors.toList());
        
        analyticsDto.setDailyAnalytics(filteredDailyAnalytics);
        
        return analyticsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getOverallAnalyticsSummary() {
        log.debug("Getting overall analytics summary");
        
        Map<String, Object> summary = new HashMap<>();
        
        // Get all active advertisements
        LocalDateTime now = LocalDateTime.now();
        List<Advertisement> activeAds = advertisementRepository.findByActiveTrueAndValidFromBeforeAndValidUntilAfter(
                now, now, null);
        
        // Calculate totals
        long totalViews = 0;
        long totalClicks = 0;
        long totalWhatsappClicks = 0;
        long totalPhoneClicks = 0;
        long totalWebsiteClicks = 0;
        long totalSocialMediaClicks = 0;
        
        Map<String, Long> clicksByDistrict = new HashMap<>();
        
        for (Advertisement ad : activeAds) {
            totalViews += ad.getViewCount();
            totalClicks += ad.getClickCount();
            totalWhatsappClicks += ad.getWhatsappClicks();
            totalPhoneClicks += ad.getPhoneClicks();
            totalWebsiteClicks += ad.getWebsiteClicks();
            totalSocialMediaClicks += ad.getSocialMediaClicks();
            
            // Add district counts
            String district = ad.getDistrictName();
            clicksByDistrict.put(district, clicksByDistrict.getOrDefault(district, 0L) + ad.getClickCount());
        }
        
        // Add totals to summary
        summary.put("totalActiveAds", activeAds.size());
        summary.put("totalViews", totalViews);
        summary.put("totalClicks", totalClicks);
        summary.put("totalWhatsappClicks", totalWhatsappClicks);
        summary.put("totalPhoneClicks", totalPhoneClicks);
        summary.put("totalWebsiteClicks", totalWebsiteClicks);
        summary.put("totalSocialMediaClicks", totalSocialMediaClicks);
        summary.put("clicksByDistrict", clicksByDistrict);
        
        // Calculate overall CTR
        double overallCTR = totalViews > 0 ? (double) totalClicks / totalViews : 0;
        summary.put("overallCTR", overallCTR);
        
        log.debug("Overall analytics summary generated");
        return summary;
    }
} 