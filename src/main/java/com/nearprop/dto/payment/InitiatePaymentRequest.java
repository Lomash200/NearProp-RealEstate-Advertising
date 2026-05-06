package com.nearprop.dto.payment;

import com.nearprop.entity.PaymentTransaction.PaymentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiatePaymentRequest {
    
    // Amount can be null for subscription payments where it's derived from the plan
    @Min(value = 0, message = "Amount must be at least 0")
    private BigDecimal amount;
    
    // Original amount before any discounts
    private BigDecimal originalAmount;
    
    @NotEmpty(message = "Currency is required")
    private String currency;
    
    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;
    
    // For subscription payments
    private Long subscriptionPlanId;
    private Boolean autoRenew;
    
    // For franchise fee payments
    private Long districtId;
    
    // Optional payment method preference
    private String preferredPaymentMethod;
    
    // Optional customer details for the payment gateway
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    
    // Optional callback URLs
    private String successUrl;
    private String failureUrl;
    private String callbackUrl;
    
    // Coupon code to apply discount
    private String couponCode;
} 