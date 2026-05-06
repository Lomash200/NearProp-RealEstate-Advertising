package com.nearprop.util;

import com.nearprop.service.DigitalSmsService;
import com.nearprop.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Utility to check SMS balance.
 * Note: This checker is now only used for email notifications,
 * not for API responses as per the requirements.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LowBalanceChecker {
    private final NotificationService notificationService;
    
    /**
     * Get the current SMS balance status
     * @return Map containing balance status information
     */
    public Map<String, Object> getSmsBalanceStatus() {
        return notificationService.getSmsBalanceStatus();
    }
}
