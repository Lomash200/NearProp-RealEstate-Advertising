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
public class PaymentVerificationResponse {
    
    private boolean success;
    private String referenceId;
    private String gatewayOrderId;
    private String gatewayTransactionId;
    private PaymentStatus status;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime paymentDate;
    private LocalDateTime completedAt;
    private String receiptUrl;
    
    // Error details if verification fails
    private String errorMessage;
    private String errorCode;
    
    // Additional payment details
    private String paymentMethod;
    private String lastFourDigits; // For card payments
    private String bank; // For net banking
    private String wallet; // For wallet payments
    private String upiId; // For UPI payments
    
    // For subscription payments
    private Long subscriptionId;
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;
} 