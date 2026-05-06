package com.nearprop.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nearprop.config.DigitalSmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class DigitalSmsService {
    private final DigitalSmsConfig digitalSmsConfig;
    private final EmailService emailService;
    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Keep track of balance state
    private boolean lowBalanceAlertSent = false;
    private int lastKnownBalance = -1;
    private AtomicBoolean isCheckingInProgress = new AtomicBoolean(false);

//
//    @Async
//    public void sendSms(String to, String message) {
//        log.info("Sending SMS to {} using Digital SMS API", to);
//
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("Accept", "application/json");
//            headers.set("Authorization","Bearer "+digitalSmsConfig.getApiKey());
//
//            Map<String, Object> requestBody = new HashMap<>();
//            requestBody.put("recipient", to);
//            requestBody.put("sender_id", digitalSmsConfig.getSenderId());
//            requestBody.put("entity_id", digitalSmsConfig.getEntityId());
//            requestBody.put("type", "transactional");
//            requestBody.put("dlt_template_id", digitalSmsConfig.getTemplateId());
//            requestBody.put("message", message);
//
//            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
//
//            ResponseEntity<String> response = restTemplate.postForEntity(
//                    digitalSmsConfig.getApiUrl(),
//                    request,
//                    String.class
//            );
//        log.info("Response:- ",response);
//            log.info("SMS sent successfully to {}, response: {}", to, response.getBody());
//
//        } catch (RestClientException e) {
//            log.error("Failed to send SMS to {}: {}", to, e.getMessage());
//            // Fallback to console in development environments
//            log.info("MOCK SMS: \"{}\" to {}", message, to);
//        }
//    }

    @Async
    public void sendSms(String to, String message) {
        log.info("Sending SMS to {} using Digital SMS API", to);

        try {
            String recipient = to.replace("+", "").trim();
            log.info("Recipient after formatting: {}", recipient);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("recipient", recipient);
            requestBody.put("sender_id", digitalSmsConfig.getSenderId());
            requestBody.put("entity_id", digitalSmsConfig.getEntityId());
            requestBody.put("type", "transactional");
            requestBody.put("dlt_template_id", digitalSmsConfig.getTemplateId());
            requestBody.put("message", message);

            String jsonBody = new ObjectMapper().writeValueAsString(requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("Authorization", digitalSmsConfig.getApiKey()); // ⚠️ No "Bearer "

            log.info("Request JSON: {}", jsonBody);

            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    digitalSmsConfig.getApiUrl(),
                    request,
                    String.class
            );

            log.info("SMS API Response: {}", response.getBody());

        } catch (Exception e) {
            log.error("Failed to send SMS: {}", e.getMessage(), e);
        }
    }



    /**
     * Check SMS balance weekly (every Saturday at 9:00 AM)
     * If balance is low, will also trigger daily checks until recharged
     */
    @Scheduled(cron = "0 0 9 * * 6") // Every Saturday at 9:00 AM
    public void checkSmsBalanceWeekly() {
        log.info("Running weekly SMS balance check");
        checkSmsBalance();
    }

    /**
     * Daily check at 9:00 AM if balance was previously low
     */
    @Scheduled(cron = "0 0 9 * * *") // Every day at 9:00 AM
    public void checkSmsBalanceDaily() {
        if (lowBalanceAlertSent) {
            log.info("Running daily SMS balance check due to previous low balance alert");
            checkSmsBalance();
        }
    }

    /**
     * Check the current SMS balance and send notifications if it's below thresholds
     */
    public synchronized void checkSmsBalance() {
        // Prevent concurrent executions
        if (!isCheckingInProgress.compareAndSet(false, true)) {
            log.info("Balance check already in progress, skipping");
            return;
        }

        try {
            log.info("Checking SMS balance");

            if (digitalSmsConfig.getBalanceUrl() == null || digitalSmsConfig.getApiKey() == null) {
                log.warn("SMS balance check not configured properly");
                return;
            }

            // Set up headers for the balance API request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            headers.set("Authorization", digitalSmsConfig.getApiKey());

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    digitalSmsConfig.getBalanceUrl(),
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                try {
                    JsonNode rootNode = objectMapper.readTree(response.getBody());

                    // Check if response indicates success
                    if (rootNode.has("status") && rootNode.get("status").asText().equals("success") &&
                            rootNode.has("data") && rootNode.get("data").has("sms_credit")) {

                        int balance = rootNode.get("data").get("sms_credit").asInt();
                        log.info("Current SMS balance: {}", balance);

                        lastKnownBalance = balance;
                        checkBalanceThresholds(balance);

                        // If balance is now above the threshold, reset the alert flag
                        if (balance > digitalSmsConfig.getLowBalanceThreshold()) {
                            if (lowBalanceAlertSent) {
                                lowBalanceAlertSent = false;
                                log.info("SMS balance is now above threshold, resetting alert state");
                            }
                        }
                    } else {
                        log.warn("Invalid balance response format: {}", rootNode);
                    }
                } catch (IOException e) {
                    log.error("Failed to parse balance response: {}", e.getMessage());
                }
            } else {
                log.error("Failed to check SMS balance, response: {}", response);
            }
        } catch (Exception e) {
            log.error("Error checking SMS balance: {}", e.getMessage(), e);
        } finally {
            isCheckingInProgress.set(false);
        }
    }

    private int extractBalance(JsonNode rootNode) {
        // Extract balance from the API response according to Digital SMS API format
        if (rootNode.has("data") && rootNode.get("data").has("sms_credit")) {
            return rootNode.get("data").get("sms_credit").asInt();
        }

        log.warn("Could not find balance in response: {}", rootNode);
        return -1;
    }

    private void checkBalanceThresholds(int balance) {
        int[] thresholds = new int[]{500, 400, 300, 200};

        for (int threshold : thresholds) {
            if (balance <= threshold) {
                String subject = "ALERT: NearProp SMS Balance Low";
                String message = String.format(
                        "NearProp SMS balance is critically low: %d credits.\n\n" +
                                "This is below the %d threshold. Please recharge soon to ensure " +
                                "uninterrupted service for user authentication and notifications.\n\n" +
                                "Current balance: %d\n" +
                                "Time of check: %s\n\n" +
                                "This is an automated notification from the NearProp system.",
                        balance, threshold, balance, LocalDateTime.now()
                );

                // Special urgent message for very low balance (below lowest threshold)
                if (balance < 200) {
                    subject = "URGENT: NearProp SMS Balance CRITICALLY Low";
                    message = String.format(
                            "⚠️ URGENT: NearProp SMS balance is CRITICALLY LOW: %d credits.\n\n" +
                                    "SMS services may be interrupted very soon! Please recharge IMMEDIATELY " +
                                    "to ensure continued authentication and notification services.\n\n" +
                                    "Current balance: %d\n" +
                                    "Time of check: %s\n\n" +
                                    "This is an automated notification from the NearProp system.",
                            balance, balance, LocalDateTime.now()
                    );
                }

                sendBalanceAlertEmails(subject, message);
                lowBalanceAlertSent = true;
                break; // Only send one alert for the highest threshold reached
            }
        }
    }

    private void sendBalanceAlertEmails(String subject, String message) {
        if (digitalSmsConfig.getNotificationEmails() == null || digitalSmsConfig.getNotificationEmails().length == 0) {
            log.warn("No notification emails configured for SMS balance alerts");
            return;
        }

        for (String email : digitalSmsConfig.getNotificationEmails()) {
            emailService.sendEmail(email, subject, message);
        }

        log.info("Sent balance alert emails to {} recipients", digitalSmsConfig.getNotificationEmails().length);
    }

    /**
     * Get the current SMS balance status
     * @return Map containing balance status information
     */
    public Map<String, Object> getSmsBalanceStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("lastCheckedBalance", lastKnownBalance);
        status.put("isLowBalance", lowBalanceAlertSent);
        status.put("threshold", digitalSmsConfig.getLowBalanceThreshold());
        return status;
    }
}
