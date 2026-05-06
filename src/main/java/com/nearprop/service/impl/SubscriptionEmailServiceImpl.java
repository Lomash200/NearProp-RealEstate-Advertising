package com.nearprop.service.impl;

import com.nearprop.entity.Property;
import com.nearprop.entity.Subscription;
import com.nearprop.entity.User;
import com.nearprop.service.SubscriptionEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionEmailServiceImpl implements SubscriptionEmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username:noreply@nearprop.com}")
    private String fromEmail;
    
    @Value("${app.url:https://nearprop.com}")
    private String appUrl;
    
    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @Override
    @Async
    public void sendSubscriptionCreatedNotification(Subscription subscription, List<Property> activatedProperties) {
        if (subscription == null) {
            log.error("Cannot send subscription created notification: subscription is null");
            return;
        }
        
        if (subscription.getUser() == null) {
            log.error("Cannot send subscription created notification: user is null for subscription ID: {}", subscription.getId());
            return;
        }
        
        log.info("Sending subscription created notification for subscription ID: {}, user: {}, with {} activated properties", 
                subscription.getId(), subscription.getUser().getEmail(), activatedProperties.size());
        
        User user = subscription.getUser();
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("subscription", subscription);
        templateVariables.put("properties", activatedProperties);
        templateVariables.put("propertyCount", activatedProperties.size());
        templateVariables.put("appUrl", appUrl);
        templateVariables.put("startDate", subscription.getStartDate().format(DATE_FORMATTER));
        templateVariables.put("endDate", subscription.getEndDate().format(DATE_FORMATTER));
        templateVariables.put("planName", subscription.getPlan().getName());
        templateVariables.put("planPrice", subscription.getPrice());
        
        String subject = "Your NearProp Subscription Has Been Created";
        String template = "subscription-created";
        
        sendEmail(user.getEmail(), subject, template, templateVariables);
    }

    @Override
    @Async
    public void sendSubscriptionExpiredNotification(Subscription subscription, List<Property> deactivatedProperties) {
        if (subscription == null) {
            log.error("Cannot send subscription expired notification: subscription is null");
            return;
        }
        
        if (subscription.getUser() == null) {
            log.error("Cannot send subscription expired notification: user is null for subscription ID: {}", subscription.getId());
            return;
        }
        
        log.info("Sending subscription expired notification for subscription ID: {}, user: {}, with {} deactivated properties", 
                subscription.getId(), subscription.getUser().getEmail(), deactivatedProperties.size());
        
        User user = subscription.getUser();
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("subscription", subscription);
        templateVariables.put("properties", deactivatedProperties);
        templateVariables.put("propertyCount", deactivatedProperties.size());
        templateVariables.put("appUrl", appUrl);
        templateVariables.put("startDate", subscription.getStartDate().format(DATE_FORMATTER));
        templateVariables.put("endDate", subscription.getEndDate().format(DATE_FORMATTER));
        templateVariables.put("planName", subscription.getPlan().getName());
        templateVariables.put("planPrice", subscription.getPrice());
        
        String subject = "Your NearProp Subscription Has Expired";
        String template = "subscription-expired";
        
        sendEmail(user.getEmail(), subject, template, templateVariables);
    }

    @Override
    @Async
    public void sendSubscriptionCancelledNotification(Subscription subscription, List<Property> deactivatedProperties) {
        if (subscription == null) {
            log.error("Cannot send subscription cancelled notification: subscription is null");
            return;
        }
        
        if (subscription.getUser() == null) {
            log.error("Cannot send subscription cancelled notification: user is null for subscription ID: {}", subscription.getId());
            return;
        }
        
        log.info("Sending subscription cancelled notification for subscription ID: {}, user: {}, with {} deactivated properties", 
                subscription.getId(), subscription.getUser().getEmail(), deactivatedProperties.size());
        
        User user = subscription.getUser();
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("subscription", subscription);
        templateVariables.put("properties", deactivatedProperties);
        templateVariables.put("propertyCount", deactivatedProperties.size());
        templateVariables.put("appUrl", appUrl);
        templateVariables.put("startDate", subscription.getStartDate().format(DATE_FORMATTER));
        templateVariables.put("endDate", subscription.getEndDate().format(DATE_FORMATTER));
        templateVariables.put("planName", subscription.getPlan().getName());
        templateVariables.put("planPrice", subscription.getPrice());
        
        String subject = "Your NearProp Subscription Has Been Cancelled";
        String template = "subscription-cancelled";
        
        sendEmail(user.getEmail(), subject, template, templateVariables);
    }

    @Override
    @Async
    public void sendSubscriptionRenewedNotification(Subscription subscription, List<Property> reactivatedProperties) {
        if (subscription == null) {
            log.error("Cannot send subscription renewed notification: subscription is null");
            return;
        }
        
        if (subscription.getUser() == null) {
            log.error("Cannot send subscription renewed notification: user is null for subscription ID: {}", subscription.getId());
            return;
        }
        
        log.info("Sending subscription renewed notification for subscription ID: {}, user: {}, with {} reactivated properties", 
                subscription.getId(), subscription.getUser().getEmail(), reactivatedProperties.size());
        
        User user = subscription.getUser();
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("subscription", subscription);
        templateVariables.put("properties", reactivatedProperties);
        templateVariables.put("propertyCount", reactivatedProperties.size());
        templateVariables.put("appUrl", appUrl);
        templateVariables.put("startDate", subscription.getStartDate().format(DATE_FORMATTER));
        templateVariables.put("endDate", subscription.getEndDate().format(DATE_FORMATTER));
        templateVariables.put("planName", subscription.getPlan().getName());
        templateVariables.put("planPrice", subscription.getPrice());
        
        String subject = "Your NearProp Subscription Has Been Renewed";
        String template = "subscription-renewed";
        
        sendEmail(user.getEmail(), subject, template, templateVariables);
    }

    @Override
    @Async
    public void sendSubscriptionExpiryWarningNotification(Subscription subscription, int daysUntilExpiry) {
        if (subscription == null) {
            log.error("Cannot send subscription expiry warning notification: subscription is null");
            return;
        }
        
        if (subscription.getUser() == null) {
            log.error("Cannot send subscription expiry warning notification: user is null for subscription ID: {}", subscription.getId());
            return;
        }
        
        log.info("Sending subscription expiry warning notification for subscription ID: {}, user: {}, days until expiry: {}", 
                subscription.getId(), subscription.getUser().getEmail(), daysUntilExpiry);
        
        User user = subscription.getUser();
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("subscription", subscription);
        templateVariables.put("daysUntilExpiry", daysUntilExpiry);
        templateVariables.put("appUrl", appUrl);
        templateVariables.put("startDate", subscription.getStartDate().format(DATE_FORMATTER));
        templateVariables.put("endDate", subscription.getEndDate().format(DATE_FORMATTER));
        templateVariables.put("planName", subscription.getPlan().getName());
        templateVariables.put("planPrice", subscription.getPrice());
        
        String subject = "Your NearProp Subscription Will Expire Soon";
        String template = "subscription-expiry-warning";
        
        sendEmail(user.getEmail(), subject, template, templateVariables);
    }

    @Override
    @Async
    public void sendSubscriptionCreatedNotification(Subscription subscription) {
        if (subscription == null) {
            log.error("Cannot send subscription created notification: subscription is null");
            return;
        }
        
        if (subscription.getUser() == null) {
            log.error("Cannot send subscription created notification: user is null for subscription ID: {}", subscription.getId());
            return;
        }
        
        log.info("Sending subscription created notification for subscription ID: {}, user: {}", 
                subscription.getId(), subscription.getUser().getEmail());
        
        User user = subscription.getUser();
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("subscription", subscription);
        templateVariables.put("propertyCount", 0);
        templateVariables.put("appUrl", appUrl);
        templateVariables.put("startDate", subscription.getStartDate().format(DATE_FORMATTER));
        templateVariables.put("endDate", subscription.getEndDate().format(DATE_FORMATTER));
        templateVariables.put("planName", subscription.getPlan().getName());
        templateVariables.put("planPrice", subscription.getPrice());
        
        String subject = "Your NearProp Subscription Has Been Created";
        String template = "subscription-created";
        
        sendEmail(user.getEmail(), subject, template, templateVariables);
    }
    
    private boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            log.warn("Cannot send email notification: email is missing");
            return false;
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            log.warn("Cannot send email notification: invalid email format: {}", email);
            return false;
        }
        
        return true;
    }
    
    private void sendEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            if (!emailEnabled) {
                log.info("Email service disabled. Would have sent email to {} with subject {}", to, subject);
                return;
            }
            
            if (!validateEmail(to)) {
                log.error("Failed to send email: Invalid recipient email address: {}", to);
                return;
            }
            
            log.info("Preparing to send email to {} with template {}", to, templateName);
            
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                
                // Set the sender email
                if (fromEmail == null || fromEmail.isEmpty() || fromEmail.equals("noreply@nearprop.com")) {
                    // Use a default value if not properly configured
                    fromEmail = "sandeep.acoreithub@gmail.com";
                    log.warn("Using default from email address: {}", fromEmail);
                }
                
                helper.setFrom(fromEmail);
                helper.setTo(to);
                helper.setSubject(subject);
                
                // Process the template
                Context context = new Context();
                context.setVariables(variables);
                
                String htmlContent;
                try {
                    log.debug("Processing email template: {}", templateName);
                    htmlContent = templateEngine.process("email/" + templateName, context);
                    
                    if (htmlContent == null || htmlContent.trim().isEmpty()) {
                        log.error("Email template processing resulted in empty content for template: {}", templateName);
                        log.error("Template variables: {}", variables);
                        return;
                    }
                    
                    log.debug("Template processed successfully, content length: {} characters", htmlContent.length());
                } catch (Exception e) {
                    log.error("Failed to process email template {}: {}", templateName, e.getMessage(), e);
                    return;
                }
                
                helper.setText(htmlContent, true);
                
                log.debug("Email content prepared for {} with template {}", to, templateName);
                
                // Send the email
                try {
                    mailSender.send(message);
                    log.info("Email sent successfully to {} with subject: {}", to, subject);
                } catch (Exception e) {
                    log.error("Failed to send email to {} with subject {}: {}", to, subject, e.getMessage(), e);
                    // Check for common SMTP issues
                    if (e.getMessage().contains("Authentication failed")) {
                        log.error("SMTP authentication failed. Please check your username and password in application-secrets.properties");
                    } else if (e.getMessage().contains("Could not connect to SMTP host")) {
                        log.error("Could not connect to SMTP host. Please check your SMTP settings and network connectivity");
                    }
                }
            } catch (MessagingException e) {
                log.error("Failed to create email message for {} with template {}: {}", to, templateName, e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("Unexpected error when sending email to {} with template {}: {}", to, templateName, e.getMessage(), e);
        }
    }
}
