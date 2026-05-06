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
public class InitiateSubscriptionPaymentDto {
    
    @NotNull(message = "Plan ID is required")
    private Long planId;
    
    private Boolean autoRenew;
    
    private String couponCode;
    
    // Optional payment preferences
    private String preferredPaymentMethod;
    
    // Optional callback URLs
    private String successUrl;
    private String failureUrl;
} 