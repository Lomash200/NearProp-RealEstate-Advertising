package com.nearprop.service.impl;

import com.nearprop.dto.PropertyDto;
import com.nearprop.entity.Property;
import com.nearprop.entity.User;
import com.nearprop.service.PropertyEmailService;
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
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyEmailServiceImpl implements PropertyEmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username:noreply@nearprop.com}")
    private String fromEmail;
    
    @Value("${app.url:https://nearprop.com}")
    private String appUrl;
    
    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;
    
    @Value("${app.admin.email:admin@nearprop.com}")
    private String adminEmail;

    @Override
    @Async
    public void sendPropertyCreatedNotification(Property property) {
        if (property == null) {
            log.error("Cannot send property created notification: property is null");
            return;
        }
        
        if (property.getOwner() == null) {
            log.error("Cannot send property created notification: owner is null for property ID: {}", property.getId());
            return;
        }
        
        log.info("Sending property created notification for property ID: {}, user: {}", 
                property.getId(), property.getOwner().getEmail());
        
        User user = property.getOwner();
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("property", property);
        templateVariables.put("appUrl", appUrl);
        
        String subject = "Your Property Listing Has Been Created";
        String template = "property-created";
        
        sendEmail(user.getEmail(), subject, template, templateVariables);
    }

    @Override
    @Async
    public void sendPropertyActivatedNotification(Property property) {
        if (property == null) {
            log.error("Cannot send property activated notification: property is null");
            return;
        }
        
        if (property.getOwner() == null) {
            log.error("Cannot send property activated notification: owner is null for property ID: {}", property.getId());
            return;
        }
        
        log.info("Sending property activated notification for property ID: {}, user: {}", 
                property.getId(), property.getOwner().getEmail());
        
        User user = property.getOwner();
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("property", property);
        templateVariables.put("appUrl", appUrl);
        
        String subject = "Your Property Listing Has Been Activated";
        String template = "property-activated";
        
        sendEmail(user.getEmail(), subject, template, templateVariables);
    }

    @Override
    @Async
    public void sendPropertyDeactivatedNotification(Property property) {
        if (property == null) {
            log.error("Cannot send property deactivated notification: property is null");
            return;
        }
        
        if (property.getOwner() == null) {
            log.error("Cannot send property deactivated notification: owner is null for property ID: {}", property.getId());
            return;
        }
        
        log.info("Sending property deactivated notification for property ID: {}, user: {}", 
                property.getId(), property.getOwner().getEmail());
        
        User user = property.getOwner();
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("property", property);
        templateVariables.put("appUrl", appUrl);
        
        String subject = "Your Property Listing Has Been Deactivated";
        String template = "property-deactivated";
        
        sendEmail(user.getEmail(), subject, template, templateVariables);
    }

    @Override
    @Async
    public void sendPropertyUpdatedNotification(Property property) {
        if (property == null) {
            log.error("Cannot send property updated notification: property is null");
            return;
        }
        
        if (property.getOwner() == null) {
            log.error("Cannot send property updated notification: owner is null for property ID: {}", property.getId());
            return;
        }
        
        log.info("Sending property updated notification for property ID: {}, user: {}", 
                property.getId(), property.getOwner().getEmail());
        
        User user = property.getOwner();
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("property", property);
        templateVariables.put("appUrl", appUrl);
        
        String subject = "Your Property Listing Has Been Updated";
        String template = "property-updated";
        
        sendEmail(user.getEmail(), subject, template, templateVariables);
    }

    @Override
    @Async
    public void sendPropertyApprovalRequestNotification(Property property) {
        if (property == null) {
            log.error("Cannot send property approval request notification: property is null");
            return;
        }
        
        log.info("Sending property approval request notification for property ID: {}", property.getId());
        
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("property", property);
        templateVariables.put("user", property.getOwner());
        templateVariables.put("appUrl", appUrl);
        
        String subject = "New Property Listing Requires Approval";
        String template = "property-approval-request";
        
        sendEmail(adminEmail, subject, template, templateVariables);
    }

    @Override
    @Async
    public void sendPropertyApprovedNotification(Property property) {
        if (property == null) {
            log.error("Cannot send property approved notification: property is null");
            return;
        }
        
        if (property.getOwner() == null) {
            log.error("Cannot send property approved notification: owner is null for property ID: {}", property.getId());
            return;
        }
        
        log.info("Sending property approved notification for property ID: {}, user: {}", 
                property.getId(), property.getOwner().getEmail());
        
        User user = property.getOwner();
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("property", property);
        templateVariables.put("appUrl", appUrl);
        
        String subject = "Your Property Listing Has Been Approved";
        String template = "property-approved";
        
        sendEmail(user.getEmail(), subject, template, templateVariables);
    }

    @Override
    @Async
    public void sendPropertyRejectedNotification(Property property, String reason) {
        if (property == null) {
            log.error("Cannot send property rejected notification: property is null");
            return;
        }
        
        if (property.getOwner() == null) {
            log.error("Cannot send property rejected notification: owner is null for property ID: {}", property.getId());
            return;
        }
        
        log.info("Sending property rejected notification for property ID: {}, user: {}", 
                property.getId(), property.getOwner().getEmail());
        
        User user = property.getOwner();
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("property", property);
        templateVariables.put("reason", reason);
        templateVariables.put("appUrl", appUrl);
        
        String subject = "Your Property Listing Has Been Rejected";
        String template = "property-rejected";
        
        sendEmail(user.getEmail(), subject, template, templateVariables);
    }
    
    @Override
    @Async
    public void sendPropertyDeactivatedNotification(Property property, String reason) {
        if (property == null) {
            log.error("Cannot send property deactivated notification: property is null");
            return;
        }
        
        if (property.getOwner() == null) {
            log.error("Cannot send property deactivated notification: owner is null for property ID: {}", property.getId());
            return;
        }
        
        log.info("Sending property deactivated by admin notification for property ID: {}, user: {}", 
                property.getId(), property.getOwner().getEmail());
        
        User user = property.getOwner();
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("property", property);
        templateVariables.put("reason", reason);
        templateVariables.put("appUrl", appUrl);
        templateVariables.put("byAdmin", true);
        
        String subject = "Your Property Listing Has Been Deactivated by Admin";
        String template = "property-deactivated-admin";
        
        sendEmail(user.getEmail(), subject, template, templateVariables);
    }
    
    @Override
    @Async
    public void sendPropertyHeldNotification(Property property, String reason) {
        if (property == null) {
            log.error("Cannot send property held notification: property is null");
            return;
        }
        
        if (property.getOwner() == null) {
            log.error("Cannot send property held notification: owner is null for property ID: {}", property.getId());
            return;
        }
        
        log.info("Sending property held notification for property ID: {}, user: {}", 
                property.getId(), property.getOwner().getEmail());
        
        User user = property.getOwner();
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("property", property);
        templateVariables.put("reason", reason);
        templateVariables.put("appUrl", appUrl);
        
        // Calculate remaining days if available
        if (property.getAdditionalDetails().containsKey("heldSubscriptionDays")) {
            templateVariables.put("remainingDays", property.getAdditionalDetails().get("heldSubscriptionDays"));
        }
        
        String subject = "Your Property Listing Has Been Temporarily Held";
        String template = "property-held";
        
        sendEmail(user.getEmail(), subject, template, templateVariables);
    }
    
    @Override
    @Async
    public void sendPropertyBlockedNotification(Property property, String reason) {
        if (property == null) {
            log.error("Cannot send property blocked notification: property is null");
            return;
        }
        
        if (property.getOwner() == null) {
            log.error("Cannot send property blocked notification: owner is null for property ID: {}", property.getId());
            return;
        }
        
        log.info("Sending property blocked notification for property ID: {}, user: {}", 
                property.getId(), property.getOwner().getEmail());
        
        User user = property.getOwner();
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("property", property);
        templateVariables.put("reason", reason);
        templateVariables.put("appUrl", appUrl);
        
        String subject = "Your Property Listing Has Been Blocked";
        String template = "property-blocked";
        
        sendEmail(user.getEmail(), subject, template, templateVariables);
    }
    
    @Override
    @Async
    public void sendPropertyUnblockedNotification(Property property) {
        if (property == null) {
            log.error("Cannot send property unblocked notification: property is null");
            return;
        }
        
        if (property.getOwner() == null) {
            log.error("Cannot send property unblocked notification: owner is null for property ID: {}", property.getId());
            return;
        }
        
        log.info("Sending property unblocked notification for property ID: {}, user: {}", 
                property.getId(), property.getOwner().getEmail());
        
        User user = property.getOwner();
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("property", property);
        templateVariables.put("appUrl", appUrl);
        
        // Include information about subscription restoration if available
        if (property.getSubscriptionExpiry() != null) {
            templateVariables.put("subscriptionExpiry", property.getSubscriptionExpiry());
        }
        
        String subject = "Your Property Listing Has Been Unblocked";
        String template = "property-unblocked";
        
        sendEmail(user.getEmail(), subject, template, templateVariables);
    }

    @Override
    @Async
    public void sendPropertyCreationEmail(String email, PropertyDto property) {
        if (property == null) {
            log.error("Cannot send developer property creation email: property is null");
            return;
        }
        
        if (!validateEmail(email)) {
            log.error("Cannot send developer property creation email: invalid email address: {}", email);
            return;
        }
        
        log.info("Sending developer property creation email for property ID: {}, email: {}", 
                property.getId(), email);
        
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("property", property);
        templateVariables.put("appUrl", appUrl);
        templateVariables.put("unitType", property.getUnitType());
        templateVariables.put("unitCount", property.getUnitCount());
        templateVariables.put("stock", property.getStock());
        
        String subject = "Your Developer Property Listing Has Been Created";
        String template = "property-created";
        
        sendEmail(email, subject, template, templateVariables);
    }
    
    @Override
    @Async
    public void sendPropertyStockUpdateEmail(String email, PropertyDto property) {
        if (property == null) {
            log.error("Cannot send property stock update email: property is null");
            return;
        }
        
        if (!validateEmail(email)) {
            log.error("Cannot send property stock update email: invalid email address: {}", email);
            return;
        }
        
        log.info("Sending property stock update email for property ID: {}, email: {}", 
                property.getId(), email);
        
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("property", property);
        templateVariables.put("appUrl", appUrl);
        templateVariables.put("unitType", property.getUnitType());
        templateVariables.put("unitCount", property.getUnitCount());
        templateVariables.put("stock", property.getStock());
        
        String subject = "Your Property Stock Has Been Updated";
        String template = "property-stock-updated";
        
        sendEmail(email, subject, template, templateVariables);
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