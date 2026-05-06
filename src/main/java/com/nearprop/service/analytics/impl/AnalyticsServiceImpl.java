package com.nearprop.service.analytics.impl;

import com.nearprop.dto.analytics.*;
import com.nearprop.entity.User;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.repository.PropertyInquiryRepository;
import com.nearprop.repository.PropertyRepository;
import com.nearprop.repository.PropertyViewRepository;
import com.nearprop.repository.ReelRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.repository.VisitRepository;
import com.nearprop.service.analytics.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PropertyRepository propertyRepository;
	@Autowired
	private PropertyInquiryRepository propertyInquiryRepository;
	@Autowired
	private VisitRepository visitRepository;
	@Autowired
	private ReelRepository reelRepository;

//    @Override
//    public DashboardOverviewDto getDashboardOverview(Long userId, String role) {
//        log.info("Generating dashboard overview for user ID: {} with role: {}", userId, role);
//        return new DashboardOverviewDto();
//    }

	@Override
	public DashboardOverviewDto getDashboardOverview(Long userId, String role) {
		log.info("Generating dashboard overview for user ID: {} with role: {}", userId, role);
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		return mapToDashboardOverviewDto(user);
	}

	@Override
	public PropertyAnalyticsDto getPropertyAnalytics(Long userId, Long propertyId, LocalDate startDate,
			LocalDate endDate) {
		log.info("Generating property analytics for user ID: {}, property ID: {} from {} to {}", userId, propertyId,
				startDate, endDate);
		return new PropertyAnalyticsDto();
	}

	@Override
	public ReelAnalyticsDto getReelAnalytics(Long userId, Long reelId, LocalDate startDate, LocalDate endDate) {
		log.info("Generating reel analytics for user ID: {}, reel ID: {} from {} to {}", userId, reelId, startDate,
				endDate);
		return new ReelAnalyticsDto();
	}

	@Override
	public VisitAnalyticsDto getVisitAnalytics(Long userId, Long propertyId, LocalDate startDate, LocalDate endDate) {
		log.info("Generating visit analytics for user ID: {}, property ID: {} from {} to {}", userId, propertyId,
				startDate, endDate);
		return new VisitAnalyticsDto();
	}

	@Override
	public SubscriptionAnalyticsDto getSubscriptionAnalytics(Long userId, LocalDate startDate, LocalDate endDate) {
		log.info("Generating subscription analytics for user ID: {} from {} to {}", userId, startDate, endDate);
		return new SubscriptionAnalyticsDto();
	}

	@Override
	public RevenueAnalyticsDto getRevenueAnalytics(Long userId, Long districtId, LocalDate startDate,
			LocalDate endDate) {
		log.info("Generating revenue analytics for user ID: {}, district ID: {} from {} to {}", userId, districtId,
				startDate, endDate);
		return new RevenueAnalyticsDto();
	}

	@Override
	public UserEngagementDto getUserEngagementAnalytics(Long userId, LocalDate startDate, LocalDate endDate) {
		log.info("Generating user engagement analytics for user ID: {} from {} to {}", userId, startDate, endDate);
		return new UserEngagementDto();
	}

	@Override
	public SystemAnalyticsDto getSystemAnalytics(LocalDate startDate, LocalDate endDate) {
		log.info("Generating system analytics from {} to {}", startDate, endDate);
		return new SystemAnalyticsDto();
	}

	@Override
	public String exportAnalytics(Long userId, String analyticsType, String format, LocalDate startDate,
			LocalDate endDate) {
		log.info("Exporting {} analytics in {} format for user ID: {} from {} to {}", analyticsType, format, userId,
				startDate, endDate);
		return "https://nearprop.com/analytics/export/" + analyticsType + "." + format;
	}

	@Override
	public Map<String, Double> getPerformanceTrends(Long userId, String metricType, String aggregation,
			LocalDate startDate, LocalDate endDate) {
		log.info("Generating {} performance trends with {} aggregation for user ID: {} from {} to {}", metricType,
				aggregation, userId, startDate, endDate);
		Map<String, Double> trends = new HashMap<>();
		trends.put("2023-01", 80.0);
		trends.put("2023-02", 85.0);
		trends.put("2023-03", 82.0);
		trends.put("2023-04", 88.0);
		trends.put("2023-05", 90.0);
		return trends;
	}

	public DashboardOverviewDto mapToDashboardOverviewDto(User user) {
	    // Count properties
	    int totalProperties = propertyRepository.countByOwnerId1(user.getId());
	    int activeProperties = propertyRepository.countByOwnerIdAndActiveTrue(user.getId());
	    int soldProperties = propertyRepository.countSoldProperties(user.getId());
	    
	    // Corrected call to count expiring properties, assuming a custom query is in place
	    int expiringSoonProperties = propertyRepository.countExpiringProperties(
	        user.getId(), 
	        LocalDateTime.now(), 
	        LocalDateTime.now().plusDays(10)
	    );

//	    // Get analytics counts
//	    long totalViews = propertyViewRepository.countViewsByOwnerId(user.getId());
//	    
	    // New code to get total reel views
	    Long totalReelViews = reelRepository.sumViewCountsByOwnerId(user.getId()).orElse(0L);
	    
	    // This method call was missing from your previous prompts,
	    // but the logs show it's a source of error.
	    //int totalInquiries = propertyInquiryRepository.countInquiriesByOwnerId(user.getId()); 
	    
	    int totalScheduledVisits = visitRepository.countScheduledAndPendingVisitsByOwnerId(user.getId());

	    return DashboardOverviewDto.builder()
	        .role(user.getRoles().toString())
	        .totalProperties(totalProperties)
	        .activeProperties(activeProperties)
	        .soldProperties(soldProperties)
	        .expiringSoonProperties(expiringSoonProperties)
	        .totalViews(totalReelViews)
	        //.totalInquiries(totalInquiries)
	        .totalScheduledVisits(totalScheduledVisits)
	        .propertiesByStatus(Map.of(
	            "TOTAL", totalProperties,
	            "ACTIVE", activeProperties,
	            "SOLD", soldProperties,
	            "EXPIRING", expiringSoonProperties
	        ))
	        .build();
	}

}