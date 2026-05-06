package com.nearprop.service;

import com.nearprop.dto.payment.InitiatePaymentRequest;
import com.nearprop.dto.payment.PaymentResponse;
import com.nearprop.dto.payment.PaymentVerificationRequest;
import com.nearprop.dto.payment.PaymentVerificationResponse;
import com.nearprop.entity.PaymentTransaction;

/**
 * Service for handling payment operations using an external payment gateway
 * This service serves as an abstraction layer between the application and
 * the payment gateway provider (like Razorpay, Stripe, PayTM, etc.)
 */
public interface PaymentService {

    /**
     * Initiate a payment transaction
     *
     * @param request Payment initiation request with payment details
     * @return Payment response with payment gateway link and transaction reference
     */
    PaymentResponse initiatePayment(InitiatePaymentRequest request);
    
    /**
     * Verify a completed payment transaction
     *
     * @param request Payment verification request with gateway-specific verification data
     * @return Payment verification response indicating success or failure
     */
    PaymentVerificationResponse verifyPayment(PaymentVerificationRequest request);
    
    /**
     * Get payment transaction details by reference ID
     *
     * @param referenceId Payment reference ID
     * @return Payment transaction details
     */
    PaymentTransaction getPaymentByReferenceId(String referenceId);
    
    /**
     * Cancel an ongoing payment transaction
     * 
     * @param referenceId Payment reference ID
     * @return True if cancellation was successful
     */
    boolean cancelPayment(String referenceId);
    
    /**
     * Check the status of a payment transaction
     *
     * @param referenceId Payment reference ID
     * @return Current status of the payment
     */
    String checkPaymentStatus(String referenceId);
    
    /**
     * Generate receipt for a successful payment
     *
     * @param referenceId Payment reference ID
     * @return Receipt link or content
     */
    String generateReceipt(String referenceId);
    
    /**
     * Process a payment refund
     *
     * @param referenceId Payment reference ID
     * @param amount Amount to refund (null for full refund)
     * @param reason Reason for refund
     * @return Refund reference ID
     */
    String processRefund(String referenceId, Double amount, String reason);
} 