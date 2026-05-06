package com.nearprop.dto.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nearprop.entity.PaymentTransaction.PaymentStatus;
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
public class PaymentResponse {
    
    private String referenceId;
    private String gatewayOrderId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    
    // URL or HTML content for payment gateway redirect
    private String paymentUrl;
    
    // For embedded payment gateways
    private String paymentToken;
    private String checkoutScript;
    
    // Additional details
    private LocalDateTime createdAt;
    private String message;
    private String receiptUrl;
    private Integer expiresInMinutes;
    
    // For subscription payments
    private Long subscriptionId;
    
    // For coupon discounts
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private String discountDetails;
    private String couponCode;
} 