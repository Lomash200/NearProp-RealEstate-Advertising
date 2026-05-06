package com.nearprop.service.impl;

import com.nearprop.dto.payment.InitiatePaymentRequest;
import com.nearprop.dto.payment.PaymentResponse;
import com.nearprop.dto.payment.PaymentVerificationRequest;
import com.nearprop.dto.payment.PaymentVerificationResponse;
import com.nearprop.entity.PaymentTransaction;
import com.nearprop.entity.PaymentTransaction.PaymentStatus;
import com.nearprop.entity.User;
import com.nearprop.exception.PaymentException;
import com.nearprop.repository.PaymentTransactionRepository;
import com.nearprop.service.PaymentService;
import com.nearprop.service.UserService;
import com.nearprop.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Mock implementation of PaymentService for testing without actual Razorpay integration
 */
@Service
@Primary // This will take precedence over the RazorpayPaymentServiceImpl
@RequiredArgsConstructor
@Slf4j
public class MockRazorpayPaymentServiceImpl implements PaymentService {

    private final PaymentTransactionRepository paymentRepository;
    private final UserService userService;
    
    @Override
    @Transactional
    public PaymentResponse initiatePayment(InitiatePaymentRequest request) {
        log.info("Initiating mock payment for amount: {} {}", request.getAmount(), request.getCurrency());
        
        User currentUser = userService.getCurrentUser();
        
        try {
            String referenceId = generateReferenceId();
            String orderId = "order_" + UUID.randomUUID().toString().replace("-", "");
            
            // Get original and final amounts from the request
            BigDecimal finalAmount = request.getAmount() != null ? request.getAmount() : BigDecimal.ZERO;
            BigDecimal originalAmount = request.getOriginalAmount() != null ? request.getOriginalAmount() : finalAmount;
            BigDecimal discountAmount = originalAmount.subtract(finalAmount);
            
            // Create payment transaction record
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .referenceId(referenceId)
                    .gatewayOrderId(orderId)
                    .user(currentUser)
                    .amount(finalAmount)
                    .originalAmount(originalAmount)
                    .currency(request.getCurrency())
                    .paymentType(request.getPaymentType())
                    .status(PaymentStatus.INITIATED)
                    .subscriptionId(request.getSubscriptionPlanId())
                    .couponCode(request.getCouponCode())
                    .discountDetails(request.getCouponCode() != null ? 
                            "Coupon applied: " + request.getCouponCode() : null)
                    .build();
            
            paymentRepository.save(transaction);
            
            // Build response with checkout information
            return PaymentResponse.builder()
                    .referenceId(referenceId)
                    .gatewayOrderId(orderId)
                    .amount(finalAmount)
                    .originalAmount(originalAmount)
                    .discountAmount(discountAmount)
                    .currency(request.getCurrency())
                    .status(PaymentStatus.INITIATED)
                    .createdAt(LocalDateTime.now())
                    .message("Payment initiated successfully")
                    .paymentToken(orderId) // Used by frontend to initiate checkout
                    .expiresInMinutes(30) // Razorpay orders expire after 30 minutes
                    .couponCode(request.getCouponCode())
                    .discountDetails(request.getCouponCode() != null ? 
                            "Discount: " + discountAmount + " " + request.getCurrency() : null)
                    .build();
            
        } catch (Exception e) {
            log.error("Error initiating payment: {}", e.getMessage(), e);
            throw new PaymentException("Failed to initiate payment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentVerificationResponse verifyPayment(PaymentVerificationRequest request) {
        log.info("Verifying mock payment for reference ID: {}", request.getReferenceId());
        
        PaymentTransaction transaction = paymentRepository.findByReferenceId(request.getReferenceId())
                .orElseThrow(() -> new PaymentException("Payment transaction not found"));
        
        try {
            // For testing, we'll just assume the payment was successful
            String transactionId = "pay_" + UUID.randomUUID().toString().replace("-", "");
            
            // Update transaction details
            transaction.setGatewayTransactionId(transactionId);
            transaction.setStatus(PaymentStatus.COMPLETED);
            transaction.setPaymentDate(LocalDateTime.now());
            transaction.setPaymentMethod(PaymentTransaction.PaymentMethod.CREDIT_CARD);
            
            // Generate receipt
            String receiptUrl = generateReceipt(transaction.getReferenceId());
            transaction.setReceiptUrl(receiptUrl);
            
            paymentRepository.save(transaction);
            
            // Build successful verification response
            return PaymentVerificationResponse.builder()
                    .success(true)
                    .referenceId(transaction.getReferenceId())
                    .gatewayTransactionId(transaction.getGatewayTransactionId())
                    .status(PaymentStatus.COMPLETED)
                    .amount(transaction.getAmount())
                    .currency(transaction.getCurrency())
                    .paymentDate(transaction.getPaymentDate())
                    .receiptUrl(receiptUrl)
                    .paymentMethod("CARD")
                    .build();
            
        } catch (Exception e) {
            log.error("Error verifying payment: {}", e.getMessage(), e);
            
            transaction.setStatus(PaymentStatus.FAILED);
            transaction.setFailureMessage(e.getMessage());
            transaction.setFailureCode("VERIFICATION_ERROR");
            paymentRepository.save(transaction);
            
            throw new PaymentException("Payment verification failed: " + e.getMessage());
        }
    }

    @Override
    public PaymentTransaction getPaymentByReferenceId(String referenceId) {
        return paymentRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new PaymentException("Payment transaction not found"));
    }

    @Override
    @Transactional
    public boolean cancelPayment(String referenceId) {
        PaymentTransaction transaction = getPaymentByReferenceId(referenceId);
        
        if (transaction.getStatus() != PaymentStatus.INITIATED) {
            throw new PaymentException("Cannot cancel payment in state: " + transaction.getStatus());
        }
        
        transaction.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(transaction);
        
        return true;
    }

    @Override
    public String checkPaymentStatus(String referenceId) {
        PaymentTransaction transaction = getPaymentByReferenceId(referenceId);
        return transaction.getStatus().toString();
    }

    @Override
    public String generateReceipt(String referenceId) {
        // In a real implementation, this would generate a PDF receipt and store it or return a URL
        // For this implementation, we'll just return a mock URL
        return "https://nearprop.com/receipts/" + referenceId + ".pdf";
    }

    @Override
    @Transactional
    public String processRefund(String referenceId, Double amount, String reason) {
        PaymentTransaction transaction = getPaymentByReferenceId(referenceId);
        
        if (transaction.getStatus() != PaymentStatus.COMPLETED) {
            throw new PaymentException("Payment cannot be refunded");
        }
        
        try {
            String refundId = "rfnd_" + UUID.randomUUID().toString().replace("-", "");
            
            // Update transaction
            if (amount != null) {
                transaction.setRefundAmount(new BigDecimal(amount));
                transaction.setStatus(transaction.getRefundAmount().compareTo(transaction.getAmount()) == 0 ? 
                        PaymentStatus.REFUNDED : PaymentStatus.PARTIALLY_REFUNDED);
            } else {
                transaction.setRefundAmount(transaction.getAmount());
                transaction.setStatus(PaymentStatus.REFUNDED);
            }
            
            if (reason != null) {
                transaction.setRefundReason(reason);
            }
            
            transaction.setRefundReferenceId(refundId);
            transaction.setRefundStatus("PROCESSED");
            
            paymentRepository.save(transaction);
            
            return refundId;
        } catch (Exception e) {
            log.error("Error processing refund: {}", e.getMessage(), e);
            throw new PaymentException("Refund processing failed: " + e.getMessage());
        }
    }
    
    // Helper methods
    private String generateReferenceId() {
        return "RANPP" + 
               LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + 
               UUID.randomUUID().toString().substring(0, 8);
    }
}