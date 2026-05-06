package com.nearprop.advertisement.service.impl;

import com.nearprop.advertisement.entity.Advertisement;
import com.nearprop.advertisement.repository.AdvertisementRepository;
import com.nearprop.advertisement.service.AdvertisementEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdvertisementExpirationScheduler {

    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementEmailService emailService;
    
    /**
     * Check for advertisements expiring in one day and send notifications
     * Runs every hour
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    @Transactional
    public void checkDayBeforeExpiration() {
        log.info("Checking for advertisements expiring in one day");
        
        LocalDateTime start = LocalDateTime.now().plusDays(1).minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1).plusHours(1);
        
        List<Advertisement> expiringAds = advertisementRepository.findAdsExpiringInOneDay(start, end);
        log.info("Found {} advertisements expiring in one day", expiringAds.size());
        
        for (Advertisement ad : expiringAds) {
            boolean emailSent = emailService.sendDayBeforeExpirationNotification(ad);
            if (emailSent) {
                ad.setDayBeforeNotificationSent(true);
                advertisementRepository.save(ad);
                log.info("Day before expiration notification sent for advertisement ID: {}", ad.getId());
            }
        }
    }
    
    /**
     * Check for advertisements expiring in five hours and send notifications
     * Runs every hour
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    @Transactional
    public void checkHoursBeforeExpiration() {
        log.info("Checking for advertisements expiring in five hours");
        
        LocalDateTime start = LocalDateTime.now().plusHours(5).minusMinutes(30);
        LocalDateTime end = LocalDateTime.now().plusHours(5).plusMinutes(30);
        
        List<Advertisement> expiringAds = advertisementRepository.findAdsExpiringInFiveHours(start, end);
        log.info("Found {} advertisements expiring in five hours", expiringAds.size());
        
        for (Advertisement ad : expiringAds) {
            boolean emailSent = emailService.sendHoursBeforeExpirationNotification(ad);
            if (emailSent) {
                ad.setHoursBeforeNotificationSent(true);
                advertisementRepository.save(ad);
                log.info("Hours before expiration notification sent for advertisement ID: {}", ad.getId());
            }
        }
    }
    
    /**
     * Check for expired advertisements and send notifications
     * Runs every hour
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    @Transactional
    public void checkExpiredAdvertisements() {
        log.info("Checking for expired advertisements");
        
        LocalDateTime now = LocalDateTime.now();
        
        List<Advertisement> expiredAds = advertisementRepository.findExpiredAdsNotNotified(now);
        log.info("Found {} expired advertisements that need notification", expiredAds.size());
        
        for (Advertisement ad : expiredAds) {
            boolean emailSent = emailService.sendExpirationNotification(ad);
            if (emailSent) {
                ad.setExpiryNotificationSent(true);
                ad.setActive(false); // Deactivate expired advertisement
                advertisementRepository.save(ad);
                log.info("Expiration notification sent and advertisement deactivated for ID: {}", ad.getId());
            }
        }
    }
} 