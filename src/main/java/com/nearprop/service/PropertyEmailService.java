package com.nearprop.service;

import com.nearprop.dto.PropertyDto;
import com.nearprop.entity.Property;
import com.nearprop.entity.User;

/**
 * Service for sending email notifications related to properties
 */
public interface PropertyEmailService {
    
    /**
     * Send notification when a property is created
     * 
     * @param property The newly created property
     */
    void sendPropertyCreatedNotification(Property property);
    
    /**
     * Send notification when a property is activated
     * 
     * @param property The activated property
     */
    void sendPropertyActivatedNotification(Property property);
    
    /**
     * Send notification when a property is deactivated
     * 
     * @param property The deactivated property
     */
    void sendPropertyDeactivatedNotification(Property property);
    
    /**
     * Send notification when a property is deactivated by admin
     * 
     * @param property The deactivated property
     * @param reason The reason for deactivation
     */
    void sendPropertyDeactivatedNotification(Property property, String reason);
    
    /**
     * Send notification when a property is updated
     * 
     * @param property The updated property
     */
    void sendPropertyUpdatedNotification(Property property);
    
    /**
     * Send notification to admin when a property requires approval
     * 
     * @param property The property requiring approval
     */
    void sendPropertyApprovalRequestNotification(Property property);
    
    /**
     * Send notification when a property is approved
     * 
     * @param property The approved property
     */
    void sendPropertyApprovedNotification(Property property);
    
    /**
     * Send notification when a property is rejected
     * 
     * @param property The rejected property
     * @param reason The reason for rejection
     */
    void sendPropertyRejectedNotification(Property property, String reason);
    
    /**
     * Send notification when a property is held by admin
     * 
     * @param property The held property
     * @param reason The reason for holding
     */
    void sendPropertyHeldNotification(Property property, String reason);
    
    /**
     * Send notification when a property is blocked by admin
     * 
     * @param property The blocked property
     * @param reason The reason for blocking
     */
    void sendPropertyBlockedNotification(Property property, String reason);
    
    /**
     * Send notification when a property is unblocked by admin
     * 
     * @param property The unblocked property
     */
    void sendPropertyUnblockedNotification(Property property);
    
    /**
     * Send confirmation email when a developer property is created
     * 
     * @param email The recipient email
     * @param property The created property
     */
    void sendPropertyCreationEmail(String email, PropertyDto property);
    
    /**
     * Send notification when a developer property's stock is updated
     * 
     * @param email The recipient email
     * @param property The updated property
     */
    void sendPropertyStockUpdateEmail(String email, PropertyDto property);
} 