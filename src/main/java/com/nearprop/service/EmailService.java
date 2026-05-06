package com.nearprop.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails through SMTP
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username:noreply@nearprop.com}")
    private String fromEmail;
    
    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;
    
    /**
     * Sends an OTP verification email asynchronously
     * 
     * @param to recipient email address
     * @param otp the one-time password to send
     */
    @Async
    public void sendOtpEmail(String to, String otp) {
        log.info("Attempting to send OTP email to: {}", to);
        log.debug("Email service enabled: {}", emailEnabled);
        
        if (!emailEnabled) {
            log.info("Email service disabled. Would have sent OTP {} to {}", otp, to);
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Your NearProp Verification Code");
            
            String emailContent = buildOtpEmailContent(otp);
            helper.setText(emailContent, true);
            
            log.debug("Sending email from: {}", fromEmail);
            mailSender.send(message);
            log.info("OTP email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}: {}", to, e.getMessage(), e);
        }
    }
    
    /**
     * Builds the HTML content for the OTP email
     * 
     * @param otp the one-time password
     * @return HTML string for the email body
     */
    private String buildOtpEmailContent(String otp) {
        StringBuilder builder = new StringBuilder();
        builder.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>");
        builder.append("<div style='background-color: #4CAF50; color: white; padding: 20px; text-align: center;'>");
        builder.append("<h1>NearProp</h1>");
        builder.append("</div>");
        builder.append("<div style='padding: 20px;'>");
        builder.append("<p>Hello,</p>");
        builder.append("<p>Thank you for registering with NearProp. To verify your email address, please use the following code:</p>");
        builder.append("<div style='background-color: #f2f2f2; padding: 15px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 5px;'>");
        builder.append(otp);
        builder.append("</div>");
        builder.append("<p>This code will expire in 10 minutes.</p>");
        builder.append("<p>If you did not request this code, please ignore this email.</p>");
        builder.append("<p>Best regards,<br>The NearProp Team</p>");
        builder.append("</div>");
        builder.append("<div style='background-color: #f2f2f2; padding: 10px; text-align: center; font-size: 12px;'>");
        builder.append("<p>&copy; 2025 NearProp. All rights reserved.</p>");
        builder.append("</div>");
        builder.append("</div>");
        
        return builder.toString();
    }
    
    /**
     * Sends a welcome email to a new user after successful registration
     * 
     * @param to recipient email address
     * @param name user's name
     */
    @Async
    public void sendWelcomeEmail(String to, String name) {
        log.info("Attempting to send welcome email to: {}", to);
        
        if (!emailEnabled) {
            log.info("Email service disabled. Would have sent welcome email to {}", to);
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Welcome to NearProp!");
            
            String emailContent = buildWelcomeEmailContent(name);
            helper.setText(emailContent, true);
            
            mailSender.send(message);
            log.info("Welcome email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to {}: {}", to, e.getMessage(), e);
        }
    }
    
    /**
     * Builds the HTML content for the welcome email
     * 
     * @param name user's name
     * @return HTML string for the email body
     */
    private String buildWelcomeEmailContent(String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>");
        builder.append("<div style='background-color: #4CAF50; color: white; padding: 20px; text-align: center;'>");
        builder.append("<h1>Welcome to NearProp!</h1>");
        builder.append("</div>");
        builder.append("<div style='padding: 20px;'>");
        builder.append("<p>Hello ").append(name).append(",</p>");
        builder.append("<p>Thank you for joining NearProp! We're excited to have you on board.</p>");
        builder.append("<p>With NearProp, you can:</p>");
        builder.append("<ul>");
        builder.append("<li>Browse properties near your location</li>");
        builder.append("<li>Schedule visits to properties</li>");
        builder.append("<li>Watch video reels of properties</li>");
        builder.append("<li>Chat with property owners</li>");
        builder.append("</ul>");
        builder.append("<p>If you have any questions, feel free to contact our support team.</p>");
        builder.append("<p>Best regards,<br>The NearProp Team</p>");
        builder.append("</div>");
        builder.append("<div style='background-color: #f2f2f2; padding: 10px; text-align: center; font-size: 12px;'>");
        builder.append("<p>&copy; 2025 NearProp. All rights reserved.</p>");
        builder.append("</div>");
        builder.append("</div>");
        
        return builder.toString();
    }
    
    /**
     * Sends a generic email with plain text content
     * 
     * @param to recipient email address
     * @param subject email subject
     * @param message plain text content
     */
    @Async
    public void sendEmail(String to, String subject, String message) {
        log.info("Attempting to send email to: {}", to);
        
        if (!emailEnabled) {
            log.info("Email service disabled. Would have sent email with subject '{}' to {}", subject, to);
            return;
        }
        
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message);
            
            mailSender.send(mimeMessage);
            log.info("Email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }
}