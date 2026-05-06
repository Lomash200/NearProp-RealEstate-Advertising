package com.nearprop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nearprop.entity.Subscription.SubscriptionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionDto {
    
    private Long id;
    
    private UserSummaryDto user;
    
    private SubscriptionPlanDto plan;
    
    private BigDecimal price;
    
    private BigDecimal marketingFee;
    
    private BigDecimal totalAmount;
    
    private Boolean isRenewal;
    
    private Long previousSubscriptionId;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private SubscriptionStatus status;
    
    private String paymentReference;
    
    private Boolean autoRenew;
    
    private LocalDateTime cancelledAt;
    
    private LocalDateTime contentHiddenAt;
    
    private LocalDateTime contentDeletedAt;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private Boolean isActive;  // Computed field
    
    private Boolean isInGracePeriod;  // Computed field
    
    private Integer daysRemaining;  // Computed field
    
    private Boolean contentVisible;
    
    // Coupon related fields
    private String couponCode;
    
    private String couponId;
    
    private BigDecimal originalPrice;
    
    private BigDecimal discountAmount;
} 