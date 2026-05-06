package com.nearprop.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "digital-sms")
@Data
@Slf4j
public class DigitalSmsConfig {
    private String apiKey;
    private String senderId;
    private String entityId;
    private String templateId;
    private String apiUrl;
    private String balanceUrl;
    private Integer lowBalanceThreshold;
    private String[] notificationEmails;
    
    @PostConstruct
    public void init() {
        if (StringUtils.hasText(apiKey) && StringUtils.hasText(senderId) && StringUtils.hasText(apiUrl)) {
            log.info("Digital SMS API configuration loaded successfully");
        } else {
            log.warn("Digital SMS API credentials not fully provided. SMS functionality may be unavailable.");
        }
        
        if (lowBalanceThreshold == null) {
            lowBalanceThreshold = 200; // Default threshold
            log.info("Setting default low balance threshold: {}", lowBalanceThreshold);
        }
    }
}
