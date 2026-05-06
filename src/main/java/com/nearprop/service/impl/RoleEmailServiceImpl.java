package com.nearprop.service.impl;

import com.nearprop.entity.RoleRequest;
import com.nearprop.service.RoleEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Implementation of the RoleEmailService for sending role-related email notifications
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleEmailServiceImpl implements RoleEmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username:noreply@nearprop.com}")
    private String fromEmail;
    
    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
    
    @Override
    @Async
    public void sendRoleRequestSubmittedNotification(RoleRequest roleRequest) {
        log.info("Sending role request submitted notification to: {}", roleRequest.getUser().getEmail());
        
        if (!emailEnabled) {
            log.info("Email service disabled. Would have sent role request submitted notification to {}", 
                    roleRequest.getUser().getEmail());
            return;
        }
        
        try {
            Context context = new Context();
            context.setVariable("userName", roleRequest.getUser().getName());
            context.setVariable("roleName", roleRequest.getRequestedRole().toString());
            context.setVariable("requestId", roleRequest.getId());
            context.setVariable("reason", roleRequest.getReason());
            context.setVariable("submittedDate", roleRequest.getCreatedAt().format(DATE_FORMATTER));
            
            String emailContent = templateEngine.process("email/role-request-submitted", context);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(roleRequest.getUser().getEmail());
            helper.setSubject("Your Role Request Has Been Submitted - NearProp");
            helper.setText(emailContent, true);
            
            mailSender.send(message);
            log.info("Role request submitted notification sent successfully to {}", roleRequest.getUser().getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send role request submitted notification to {}: {}", 
                    roleRequest.getUser().getEmail(), e.getMessage(), e);
        }
    }
    
    @Override
    @Async
    public void sendRoleRequestApprovedNotification(RoleRequest roleRequest) {
        log.info("Sending role request approved notification to: {}", roleRequest.getUser().getEmail());
        
        if (!emailEnabled) {
            log.info("Email service disabled. Would have sent role request approved notification to {}", 
                    roleRequest.getUser().getEmail());
            return;
        }
        
        try {
            Context context = new Context();
            context.setVariable("userName", roleRequest.getUser().getName());
            context.setVariable("roleName", roleRequest.getRequestedRole().toString());
            context.setVariable("requestId", roleRequest.getId());
            context.setVariable("processedDate", roleRequest.getProcessedAt().format(DATE_FORMATTER));
            context.setVariable("comment", roleRequest.getAdminComment());
            
            String emailContent = templateEngine.process("email/role-request-approved", context);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(roleRequest.getUser().getEmail());
            helper.setSubject("Your Role Request Has Been Approved - NearProp");
            helper.setText(emailContent, true);
            
            mailSender.send(message);
            log.info("Role request approved notification sent successfully to {}", roleRequest.getUser().getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send role request approved notification to {}: {}", 
                    roleRequest.getUser().getEmail(), e.getMessage(), e);
        }
    }
    
    @Override
    @Async
    public void sendRoleRequestRejectedNotification(RoleRequest roleRequest) {
        log.info("Sending role request rejected notification to: {}", roleRequest.getUser().getEmail());
        
        if (!emailEnabled) {
            log.info("Email service disabled. Would have sent role request rejected notification to {}", 
                    roleRequest.getUser().getEmail());
            return;
        }
        
        try {
            Context context = new Context();
            context.setVariable("userName", roleRequest.getUser().getName());
            context.setVariable("roleName", roleRequest.getRequestedRole().toString());
            context.setVariable("requestId", roleRequest.getId());
            context.setVariable("processedDate", roleRequest.getProcessedAt().format(DATE_FORMATTER));
            context.setVariable("comment", roleRequest.getAdminComment());
            
            String emailContent = templateEngine.process("email/role-request-rejected", context);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(roleRequest.getUser().getEmail());
            helper.setSubject("Your Role Request Status Update - NearProp");
            helper.setText(emailContent, true);
            
            mailSender.send(message);
            log.info("Role request rejected notification sent successfully to {}", roleRequest.getUser().getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send role request rejected notification to {}: {}", 
                    roleRequest.getUser().getEmail(), e.getMessage(), e);
        }
    }
} 