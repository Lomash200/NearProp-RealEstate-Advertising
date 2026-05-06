package com.nearprop.controller.analytics;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.analytics.*;
import com.nearprop.service.UserService;
import com.nearprop.service.analytics.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UserService userService;
    
    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DashboardOverviewDto>> getDashboardOverview(
            @RequestParam(required = false) String role) {
        
        Long userId = userService.getCurrentUser().getId();
        String userRole = role != null ? role : "USER"; // Default to USER role if not specified
        
        log.info("Fetching dashboard overview for user ID: {} with role: {}", userId, userRole);
        
        DashboardOverviewDto overview = analyticsService.getDashboardOverview(userId, userRole);
        
        return ResponseEntity.ok(ApiResponse.success("Dashboard overview retrieved successfully", overview));
    }
    
    @GetMapping("/properties")
    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PropertyAnalyticsDto>> getPropertyAnalytics(
            @RequestParam(required = false) Long propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Long userId = userService.getCurrentUser().getId();
        
        log.info("Fetching property analytics for user ID: {}, property ID: {} from {} to {}", 
                userId, propertyId, startDate, endDate);
        
        PropertyAnalyticsDto analytics = analyticsService.getPropertyAnalytics(userId, propertyId, startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success("Property analytics retrieved successfully", analytics));
    }
    
    @GetMapping("/reels")
    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReelAnalyticsDto>> getReelAnalytics(
            @RequestParam(required = false) Long reelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Long userId = userService.getCurrentUser().getId();
        
        log.info("Fetching reel analytics for user ID: {}, reel ID: {} from {} to {}", 
                userId, reelId, startDate, endDate);
        
        ReelAnalyticsDto analytics = analyticsService.getReelAnalytics(userId, reelId, startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success("Reel analytics retrieved successfully", analytics));
    }
    
    @GetMapping("/visits")
    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<ApiResponse<VisitAnalyticsDto>> getVisitAnalytics(
            @RequestParam(required = false) Long propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Long userId = userService.getCurrentUser().getId();
        
        log.info("Fetching visit analytics for user ID: {}, property ID: {} from {} to {}", 
                userId, propertyId, startDate, endDate);
        
        VisitAnalyticsDto analytics = analyticsService.getVisitAnalytics(userId, propertyId, startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success("Visit analytics retrieved successfully", analytics));
    }
    
    @GetMapping("/subscriptions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SubscriptionAnalyticsDto>> getSubscriptionAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Long userId = userService.getCurrentUser().getId();
        
        log.info("Fetching subscription analytics for user ID: {} from {} to {}", userId, startDate, endDate);
        
        SubscriptionAnalyticsDto analytics = analyticsService.getSubscriptionAnalytics(userId, startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success("Subscription analytics retrieved successfully", analytics));
    }
    
    @GetMapping("/revenue")
    @PreAuthorize("hasAnyRole('FRANCHISEE', 'ADMIN')")
    public ResponseEntity<ApiResponse<RevenueAnalyticsDto>> getRevenueAnalytics(
            @RequestParam(required = false) Long districtId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Long userId = userService.getCurrentUser().getId();
        
        log.info("Fetching revenue analytics for user ID: {}, district ID: {} from {} to {}", 
                userId, districtId, startDate, endDate);
        
        RevenueAnalyticsDto analytics = analyticsService.getRevenueAnalytics(userId, districtId, startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success("Revenue analytics retrieved successfully", analytics));
    }
    
    @GetMapping("/engagement")
    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserEngagementDto>> getUserEngagementAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Long userId = userService.getCurrentUser().getId();
        
        log.info("Fetching user engagement analytics for user ID: {} from {} to {}", userId, startDate, endDate);
        
        UserEngagementDto analytics = analyticsService.getUserEngagementAnalytics(userId, startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success("User engagement analytics retrieved successfully", analytics));
    }
    
    @GetMapping("/system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SystemAnalyticsDto>> getSystemAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Fetching system analytics from {} to {}", startDate, endDate);
        
        SystemAnalyticsDto analytics = analyticsService.getSystemAnalytics(startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success("System analytics retrieved successfully", analytics));
    }
    
    @GetMapping("/export")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> exportAnalytics(
            @RequestParam String analyticsType,
            @RequestParam String format,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Long userId = userService.getCurrentUser().getId();
        
        log.info("Exporting {} analytics in {} format for user ID: {} from {} to {}", 
                analyticsType, format, userId, startDate, endDate);
        
        String exportUrl = analyticsService.exportAnalytics(userId, analyticsType, format, startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success("Analytics exported successfully", exportUrl));
    }
    
    @GetMapping("/trends")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Double>>> getPerformanceTrends(
            @RequestParam String metricType,
            @RequestParam String aggregation,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Long userId = userService.getCurrentUser().getId();
        
        log.info("Fetching {} performance trends with {} aggregation for user ID: {} from {} to {}", 
                metricType, aggregation, userId, startDate, endDate);
        
        Map<String, Double> trends = analyticsService.getPerformanceTrends(
                userId, metricType, aggregation, startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success("Performance trends retrieved successfully", trends));
    }
} 