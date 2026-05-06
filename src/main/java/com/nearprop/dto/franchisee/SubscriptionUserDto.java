package com.nearprop.dto.franchisee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for subscription user data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionUserDto {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String profileImageUrl;
    
    // Subscription details
    private Long subscriptionId;
    private String subscriptionPlan;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // ACTIVE, EXPIRED, CANCELLED, etc.
    private BigDecimal amount;
    private String paymentMethod;
    
    // Property details (if subscription is for a specific property)
    private Long propertyId;
    private String propertyTitle;
    private String propertyType;
    private String propertyLocation;
    
    // Activity metrics
    private LocalDateTime lastLoginAt;
    private int totalLogins;
    private int totalPropertyViews;
    private int totalEnquiries;
    
    // Renewal information
    private boolean autoRenewal;
    private int renewalCount;
    private LocalDate nextRenewalDate;
} 