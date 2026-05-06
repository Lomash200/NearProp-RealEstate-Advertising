package com.nearprop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionDto {
    
    @NotNull(message = "Plan ID is required")
    private Long planId;
    
    private Boolean autoRenew;
    
    private Boolean isRenewal;
    
    private Long previousSubscriptionId;
    
    // This will be replaced with actual payment gateway integration
    private String paymentMethod;  // e.g., "CREDIT_CARD", "UPI", etc.
    
    private String paymentReferenceId;
    
    // Coupon code to apply discount
    private String couponCode;
    
    // District ID for revenue tracking
    @NotNull(message = "District ID is required")
    private Long districtId;
} 