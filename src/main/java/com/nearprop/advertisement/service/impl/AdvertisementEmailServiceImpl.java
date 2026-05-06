package com.nearprop.advertisement.service.impl;

import com.nearprop.advertisement.entity.Advertisement;
import com.nearprop.advertisement.service.AdvertisementEmailService;
import com.nearprop.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvertisementEmailServiceImpl implements AdvertisementEmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username:noreply@nearprop.com}")
    private String fromEmail;
    
    @Value("${app.url:http://localhost:8080}")
    private String appUrl;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    
    @Override
    public boolean sendDayBeforeExpirationNotification(Advertisement advertisement) {
        log.info("Sending day before expiration notification for advertisement ID: {}", advertisement.getId());
        
        User advertiser = advertisement.getCreatedBy();
        String to = advertiser.getEmail();
        
        if (to == null || to.isEmpty()) {
            log.warn("Cannot send email notification: advertiser email is missing for advertisement ID: {}", advertisement.getId());
            return false;
        }
        
        String subject = "Your Advertisement Will Expire in 24 Hours - NearProp";
        String message = String.format(
            "Dear %s,\n\n" +
            "Your advertisement '%s' (ID: %d) will expire in 24 hours on %s.\n\n" +
            "To view your advertisement, visit: %s/advertisements/%d\n\n" +
            "To renew your advertisement, visit: %s/advertisements/renew/%d\n\n" +
            "Thank you for using NearProp!\n\n" +
            "Best regards,\n" +
            "The NearProp Team",
            advertiser.getName(),
            advertisement.getTitle(),
            advertisement.getId(),
            advertisement.getValidUntil().format(DATE_FORMATTER),
            appUrl, advertisement.getId(),
            appUrl, advertisement.getId()
        );
        
        return sendEmail(to, subject, message);
    }
    
    @Override
    public boolean sendHoursBeforeExpirationNotification(Advertisement advertisement) {
        log.info("Sending hours before expiration notification for advertisement ID: {}", advertisement.getId());
        
        User advertiser = advertisement.getCreatedBy();
        String to = advertiser.getEmail();
        
        if (to == null || to.isEmpty()) {
            log.warn("Cannot send email notification: advertiser email is missing for advertisement ID: {}", advertisement.getId());
            return false;
        }
        
        String subject = "URGENT: Your Advertisement Will Expire in 5 Hours - NearProp";
        String message = String.format(
            "Dear %s,\n\n" +
            "URGENT: Your advertisement '%s' (ID: %d) will expire in 5 hours on %s.\n\n" +
            "To view your advertisement, visit: %s/advertisements/%d\n\n" +
            "To renew your advertisement, visit: %s/advertisements/renew/%d\n\n" +
            "Thank you for using NearProp!\n\n" +
            "Best regards,\n" +
            "The NearProp Team",
            advertiser.getName(),
            advertisement.getTitle(),
            advertisement.getId(),
            advertisement.getValidUntil().format(DATE_FORMATTER),
            appUrl, advertisement.getId(),
            appUrl, advertisement.getId()
        );
        
        return sendEmail(to, subject, message);
    }
    
    @Override
    public boolean sendExpirationNotification(Advertisement advertisement) {
        log.info("Sending expiration notification for advertisement ID: {}", advertisement.getId());
        
        User advertiser = advertisement.getCreatedBy();
        String to = advertiser.getEmail();
        
        if (to == null || to.isEmpty()) {
            log.warn("Cannot send email notification: advertiser email is missing for advertisement ID: {}", advertisement.getId());
            return false;
        }
        
        String subject = "Your Advertisement Has Expired - NearProp";
        String message = String.format(
            "Dear %s,\n\n" +
            "Your advertisement '%s' (ID: %d) has expired on %s.\n\n" +
            "Advertisement Performance:\n" +
            "- Views: %d\n" +
            "- Clicks: %d\n" +
            "- WhatsApp Clicks: %d\n" +
            "- Phone Clicks: %d\n" +
            "- Website Clicks: %d\n" +
            "- Social Media Clicks: %d\n\n" +
            "To renew your advertisement, visit: %s/advertisements/renew/%d\n\n" +
            "To view detailed analytics, visit: %s/advertisements/analytics/%d\n\n" +
            "Thank you for using NearProp!\n\n" +
            "Best regards,\n" +
            "The NearProp Team",
            advertiser.getName(),
            advertisement.getTitle(),
            advertisement.getId(),
            advertisement.getValidUntil().format(DATE_FORMATTER),
            advertisement.getViewCount(),
            advertisement.getClickCount(),
            advertisement.getWhatsappClicks(),
            advertisement.getPhoneClicks(),
            advertisement.getWebsiteClicks(),
            advertisement.getSocialMediaClicks(),
            appUrl, advertisement.getId(),
            appUrl, advertisement.getId()
        );
        
        return sendEmail(to, subject, message);
    }
    
    private boolean sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {} with subject: {}", to, subject);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to: {} with subject: {}", to, subject, e);
            return false;
        }
    }
} 