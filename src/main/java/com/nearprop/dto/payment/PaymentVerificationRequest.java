package com.nearprop.dto.payment;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationRequest {
    
    @NotEmpty(message = "Reference ID is required")
    private String referenceId;
    
    // Gateway-specific verification data
    private String gatewayTransactionId;
    private String gatewayOrderId;
    private String paymentSignature; // For signature verification
    
    // For webhook callbacks
    private Map<String, String> callbackParams;
} 