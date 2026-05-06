package com.nearprop.service;

import com.nearprop.entity.Property;
import com.nearprop.entity.Subscription;

import java.util.List;

/**
 * Service for sending email notifications related to subscriptions
 */
public interface SubscriptionEmailService {
    
    /**
     * Send notification when a subscription is created (without activated properties)
     * 
     * @param subscription The newly created subscription
     */
    void sendSubscriptionCreatedNotification(Subscription subscription);
    
    /**
     * Send notification when a subscription is created
     * 
     * @param subscription The newly created subscription
     * @param activatedProperties List of properties that were activated
     */
    void sendSubscriptionCreatedNotification(Subscription subscription, List<Property> activatedProperties);
    
    /**
     * Send notification when a subscription is renewed
     * 
     * @param subscription The renewed subscription
     * @param reactivatedProperties List of properties that were reactivated
     */
    void sendSubscriptionRenewedNotification(Subscription subscription, List<Property> reactivatedProperties);
    
    /**
     * Send notification when a subscription has expired
     * 
     * @param subscription The expired subscription
     * @param deactivatedProperties List of properties that were deactivated
     */
    void sendSubscriptionExpiredNotification(Subscription subscription, List<Property> deactivatedProperties);
    
    /**
     * Send notification when a subscription is cancelled
     * 
     * @param subscription The cancelled subscription
     * @param deactivatedProperties List of properties that were deactivated
     */
    void sendSubscriptionCancelledNotification(Subscription subscription, List<Property> deactivatedProperties);
    
    /**
     * Send warning notification before subscription expires
     * 
     * @param subscription The subscription that will expire soon
     * @param daysUntilExpiry Number of days until the subscription expires
     */
    void sendSubscriptionExpiryWarningNotification(Subscription subscription, int daysUntilExpiry);
} 