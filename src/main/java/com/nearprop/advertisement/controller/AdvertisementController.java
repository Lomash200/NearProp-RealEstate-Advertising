package com.nearprop.advertisement.controller;

import com.nearprop.advertisement.dto.AdvertisementAnalyticsDto;
import com.nearprop.advertisement.dto.AdvertisementDto;
import com.nearprop.advertisement.dto.CreateAdvertisementDto;
import com.nearprop.advertisement.entity.AdvertisementClick.ClickType;
import com.nearprop.advertisement.service.AdvertisementAnalyticsService;
import com.nearprop.advertisement.service.AdvertisementService;
import com.nearprop.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/advertisements")
@RequiredArgsConstructor
@Slf4j
public class AdvertisementController {
    
    private final AdvertisementService advertisementService;
    private final AdvertisementAnalyticsService analyticsService;
    
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdvertisementDto> createAdvertisement(
            @Valid @ModelAttribute CreateAdvertisementDto dto,
            @AuthenticationPrincipal User currentUser) {
        log.info("Creating advertisement request received from user ID: {}", currentUser.getId());
        AdvertisementDto createdAd = advertisementService.createAdvertisement(dto, currentUser.getId());
        log.info("Advertisement created with ID: {}", createdAd.getId());
        return new ResponseEntity<>(createdAd, HttpStatus.CREATED);
    }
    
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdvertisementDto> updateAdvertisement(
            @PathVariable Long id,
            @Valid @ModelAttribute CreateAdvertisementDto dto,
            @AuthenticationPrincipal User currentUser) {
        log.info("Updating advertisement ID: {} request received from user ID: {}", id, currentUser.getId());
        AdvertisementDto updatedAd = advertisementService.updateAdvertisement(id, dto, currentUser.getId());
        log.info("Advertisement updated with ID: {}", updatedAd.getId());
        return ResponseEntity.ok(updatedAd);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementDto> getAdvertisement(
            @PathVariable Long id,
            HttpServletRequest request) {
        log.info("Fetching advertisement ID: {}", id);
        AdvertisementDto ad = advertisementService.getAdvertisement(id);
        
        // Record view
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referrer = request.getHeader("Referer");
        String userDistrict = request.getParameter("district"); // Get district from request param if available
        
        // Get user ID if authenticated
        User currentUser = (User) request.getAttribute("currentUser");
        Long userId = currentUser != null ? currentUser.getId() : null;
        
        // Record view asynchronously
        analyticsService.recordView(id, userId, ipAddress, userAgent, userDistrict, referrer);
        
        return ResponseEntity.ok(ad);
    }
    
    @GetMapping
    public ResponseEntity<Page<AdvertisementDto>> getAllActiveAdvertisements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        log.info("Fetching all active advertisements, page: {}, size: {}, sortBy: {}, direction: {}", 
                page, size, sortBy, direction);
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdvertisementDto> ads = advertisementService.getAllActiveAdvertisements(pageable);
        log.debug("Retrieved {} active advertisements", ads.getTotalElements());
        return ResponseEntity.ok(ads);
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Page<AdvertisementDto>> getUserAdvertisements(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        log.info("Fetching advertisements for user with ID: {}, page: {}, size: {}", 
                userId, page, size);
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdvertisementDto> ads = advertisementService.getUserAdvertisements(userId, pageable);
        log.debug("Retrieved {} advertisements for user ID: {}", ads.getTotalElements(), userId);
        return ResponseEntity.ok(ads);
    }
    
    @GetMapping("/district/{districtName}")
    public ResponseEntity<Page<AdvertisementDto>> getAdvertisementsByDistrict(
            @PathVariable String districtName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        log.info("Fetching advertisements for district: {}, page: {}, size: {}", 
                districtName, page, size);
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdvertisementDto> ads = advertisementService.getAdvertisementsByDistrict(districtName, pageable);
        log.debug("Retrieved {} advertisements for district: {}", ads.getTotalElements(), districtName);
        return ResponseEntity.ok(ads);
    }
    
    @GetMapping("/district/{districtName}/top5")
    public ResponseEntity<List<AdvertisementDto>> getTop5AdvertisementsByDistrict(
            @PathVariable String districtName) {
        log.info("Fetching top 5 advertisements for district: {}", districtName);
        List<AdvertisementDto> ads = advertisementService.getTop5AdvertisementsByDistrict(districtName);
        log.debug("Retrieved {} top advertisements for district: {}", ads.size(), districtName);
        return ResponseEntity.ok(ads);
    }
    
    @GetMapping("/nearby")
    public ResponseEntity<Page<AdvertisementDto>> getAdvertisementsNearLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching advertisements near location: lat={}, long={}, page: {}, size: {}", 
                latitude, longitude, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<AdvertisementDto> ads = advertisementService.getAdvertisementsNearLocation(latitude, longitude, pageable);
        log.debug("Retrieved {} advertisements near location", ads.getTotalElements());
        return ResponseEntity.ok(ads);
    }
    
    @PutMapping("/{id}/active/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdvertisementDto> setAdvertisementActive(
            @PathVariable Long id,
            @PathVariable boolean status,
            @AuthenticationPrincipal User currentUser) {
        log.info("Setting advertisement ID: {} active status to: {} by user ID: {}", 
                id, status, currentUser.getId());
        AdvertisementDto ad = advertisementService.setAdvertisementActive(id, status, currentUser.getId());
        log.info("Advertisement ID: {} active status updated to: {}", id, status);
        return ResponseEntity.ok(ad);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAdvertisement(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        log.info("Deleting advertisement ID: {} by user ID: {}", id, currentUser.getId());
        advertisementService.deleteAdvertisement(id, currentUser.getId());
        log.info("Advertisement ID: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
    
    // Analytics endpoints
    
    @GetMapping("/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AdvertisementAnalyticsDto>> getAllAdvertisementsAnalytics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching analytics for all advertisements, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<AdvertisementAnalyticsDto> analytics = analyticsService.getAllAdvertisementsAnalytics(pageable);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/{id}/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdvertisementAnalyticsDto> getAdvertisementAnalytics(
            @PathVariable Long id) {
        log.info("Fetching analytics for advertisement ID: {}", id);
        AdvertisementAnalyticsDto analytics = analyticsService.getAdvertisementAnalytics(id);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/analytics/district/{districtName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdvertisementAnalyticsDto>> getDistrictAdvertisementsAnalytics(
            @PathVariable String districtName) {
        log.info("Fetching analytics for advertisements in district: {}", districtName);
        List<AdvertisementAnalyticsDto> analytics = analyticsService.getDistrictAdvertisementsAnalytics(districtName);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/analytics/social-media")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getSocialMediaClicksAnalytics() {
        log.info("Fetching social media clicks analytics");
        Map<String, Long> analytics = analyticsService.getSocialMediaClicksAnalytics();
        return ResponseEntity.ok(analytics);
    }
    
    // Click tracking endpoints
    @PostMapping("/{id}/click/website")
    public ResponseEntity<Void> recordWebsiteClick(
            @PathVariable Long id,
            HttpServletRequest request) {
        recordClick(id, ClickType.WEBSITE, request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/click/whatsapp")
    public ResponseEntity<Void> recordWhatsappClick(
            @PathVariable Long id,
            HttpServletRequest request) {
        recordClick(id, ClickType.WHATSAPP, request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/click/phone")
    public ResponseEntity<Void> recordPhoneClick(
            @PathVariable Long id,
            HttpServletRequest request) {
        recordClick(id, ClickType.PHONE, request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/click/instagram")
    public ResponseEntity<Void> recordInstagramClick(
            @PathVariable Long id,
            HttpServletRequest request) {
        recordClick(id, ClickType.INSTAGRAM, request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/click/facebook")
    public ResponseEntity<Void> recordFacebookClick(
            @PathVariable Long id,
            HttpServletRequest request) {
        recordClick(id, ClickType.FACEBOOK, request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/click/youtube")
    public ResponseEntity<Void> recordYoutubeClick(
            @PathVariable Long id,
            HttpServletRequest request) {
        recordClick(id, ClickType.YOUTUBE, request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/click/twitter")
    public ResponseEntity<Void> recordTwitterClick(
            @PathVariable Long id,
            HttpServletRequest request) {
        recordClick(id, ClickType.TWITTER, request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/click/linkedin")
    public ResponseEntity<Void> recordLinkedinClick(
            @PathVariable Long id,
            HttpServletRequest request) {
        recordClick(id, ClickType.LINKEDIN, request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/click")
    public ResponseEntity<Void> recordGenericClick(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "OTHER") String type,
            HttpServletRequest request) {
        ClickType clickType;
        try {
            clickType = ClickType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            clickType = ClickType.OTHER;
        }
        recordClick(id, clickType, request);
        return ResponseEntity.ok().build();
    }
    
    private void recordClick(Long advertisementId, ClickType clickType, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referrer = request.getHeader("Referer");
        String userDistrict = request.getParameter("district"); // Get district from request param if available
        
        // Get user ID if authenticated
        User currentUser = (User) request.getAttribute("currentUser");
        Long userId = currentUser != null ? currentUser.getId() : null;
        
        // Record click asynchronously
        analyticsService.recordClick(advertisementId, clickType, userId, ipAddress, userAgent, userDistrict, referrer);
        log.debug("Recorded {} click for advertisement ID: {}", clickType, advertisementId);
    }
} 