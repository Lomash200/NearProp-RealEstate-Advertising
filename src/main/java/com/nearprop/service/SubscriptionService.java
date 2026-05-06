package com.nearprop.service;

import com.nearprop.dto.CreateSubscriptionDto;
import com.nearprop.dto.SubscriptionDto;
import com.nearprop.dto.SubscriptionPlanDto;
import com.nearprop.entity.Subscription;
import com.nearprop.entity.SubscriptionPlan.PlanType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscriptionService {
    
    /**
     * Get all available subscription plans of a specific type
     * 
     * @param type Plan type
     * @return List of subscription plans
     */
    List<SubscriptionPlanDto> getSubscriptionPlans(PlanType type);
    
    /**
     * Get all available subscription plans
     * 
     * @return List of subscription plans
     */
    List<SubscriptionPlanDto> getAllSubscriptionPlans();
    
    /**
     * Create a new subscription for the current user
     * 
     * @param createDto Subscription details
     * @return The created subscription
     */
    SubscriptionDto createSubscription(CreateSubscriptionDto createDto);
    
    /**
     * Get subscription by ID
     * 
     * @param subscriptionId Subscription ID
     * @return Subscription details
     */
    SubscriptionDto getSubscription(Long subscriptionId);
    
    /**
     * Get current user's subscriptions
     * 
     * @param pageable Pagination information
     * @return Page of subscriptions
     */
    Page<SubscriptionDto> getUserSubscriptions(Pageable pageable);
    
    /**
     * Cancel a subscription
     * 
     * @param subscriptionId Subscription ID
     */
    void cancelSubscription(Long subscriptionId);
    
    /**
     * Renew an existing subscription
     * 
     * @param subscriptionId Subscription ID
     * @return The renewed subscription
     */
    SubscriptionDto renewSubscription(Long subscriptionId);
    
    /**
     * Check if a user has an active subscription of a specific type
     * 
     * @param userId User ID
     * @param planType Plan type
     * @return True if user has an active subscription
     */
    boolean hasActiveSubscription(Long userId, PlanType planType);
    
    /**
     * Process expired subscriptions
     * This method is scheduled to run periodically
     */
    void processExpiredSubscriptions();
    
    /**
     * Get the number of subscriptions for a user of a specific type
     * 
     * @param userId User ID
     * @param planType Plan type
     * @return Number of subscriptions
     */
    long getSubscriptionCount(Long userId, PlanType planType);
    
    /**
     * Get the expiry date of a property subscription for a user
     * 
     * @param userId User ID
     * @return Expiry date or null if no active subscription
     */
    LocalDateTime getPropertySubscriptionExpiryDate(Long userId);
    
    /**
     * Check if a user can add more properties based on their subscription limits
     * 
     * @param userId User ID
     * @return True if user can add more properties
     */
    boolean canAddMoreProperties(Long userId);
    
    /**
     * Get a subscription that has available property slots
     * 
     * @param userId User ID
     * @param planType Plan type
     * @return Optional subscription
     */
    Optional<Subscription> getSubscriptionWithAvailablePropertySlots(Long userId, PlanType planType);
    
    /**
     * Count properties associated with a subscription
     * 
     * @param subscriptionId Subscription ID
     * @return Number of properties
     */
    long countPropertiesBySubscriptionId(Long subscriptionId);
    
    /**
     * Apply a coupon code to a subscription plan
     * 
     * @param planId Subscription plan ID
     * @param couponCode Coupon code
     * @return Discounted price information
     */
    com.nearprop.dto.CouponValidationResponseDto applyCouponToSubscriptionPlan(Long planId, String couponCode);
    
    /**
     * Confirm payment for a subscription
     * 
     * @param subscriptionId Subscription ID
     * @param paymentReferenceId Payment reference ID from payment gateway
     */
    void confirmPayment(Long subscriptionId, String paymentReferenceId);
    boolean hasActiveProfileSubscription(Long userId);

}