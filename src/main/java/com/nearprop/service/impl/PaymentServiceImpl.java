package com.nearprop.service.impl;

import com.nearprop.dto.CouponValidationDto;
import com.nearprop.dto.CouponValidationResponseDto;
import com.nearprop.dto.payment.InitiatePaymentRequest;
import com.nearprop.dto.payment.PaymentResponse;
import com.nearprop.dto.payment.PaymentVerificationRequest;
import com.nearprop.dto.payment.PaymentVerificationResponse;
import com.nearprop.entity.PaymentTransaction;
import com.nearprop.entity.PaymentTransaction.PaymentStatus;
import com.nearprop.entity.PaymentTransaction.PaymentType;
import com.nearprop.entity.SubscriptionPlan;
import com.nearprop.entity.User;
import com.nearprop.exception.BadRequestException;
import com.nearprop.exception.PaymentException;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.repository.PaymentTransactionRepository;
import com.nearprop.repository.SubscriptionPlanRepository;
import com.nearprop.service.CouponService;
import com.nearprop.service.PaymentService;
import com.nearprop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentTransactionRepository paymentRepository;
    private final UserService userService;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final CouponService couponService;
    
    @Override
    @Transactional
    public PaymentResponse initiatePayment(InitiatePaymentRequest request) {
        log.info("Initiating payment for request: {}", request);
        
        User currentUser = userService.getCurrentUser();
        BigDecimal finalAmount = request.getAmount() != null ? request.getAmount() : BigDecimal.ZERO;
        BigDecimal originalAmount = request.getOriginalAmount() != null ? request.getOriginalAmount() : finalAmount;
        BigDecimal discountAmount = BigDecimal.ZERO; // Default to zero
        String discountDetails = null;
        
        // Handle subscription plan pricing and coupon code if provided
        if (request.getPaymentType() == PaymentType.SUBSCRIPTION && request.getSubscriptionPlanId() != null) {
            // Get subscription plan
            SubscriptionPlan plan = subscriptionPlanRepository.findById(request.getSubscriptionPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with ID: " + request.getSubscriptionPlanId()));
            
            // Set amount based on plan price
            finalAmount = plan.getPrice();
            originalAmount = plan.getPrice(); // Store original plan price
            
            log.info("Using plan price: {} for plan ID: {}", plan.getPrice(), plan.getId());
            
            // Apply coupon if provided
            if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
                CouponValidationDto validationDto = new CouponValidationDto();
                validationDto.setCode(request.getCouponCode());
                validationDto.setPlanId(request.getSubscriptionPlanId());
                
                log.info("Validating coupon code: {} for plan ID: {}", request.getCouponCode(), request.getSubscriptionPlanId());
                CouponValidationResponseDto couponResult = couponService.validateCoupon(validationDto);
                
                log.info("Coupon validation result: valid={}, originalPrice={}, finalPrice={}, discountAmount={}, message={}",
                        couponResult.isValid(), couponResult.getOriginalPrice(), couponResult.getFinalPrice(),
                        couponResult.getDiscountAmount(), couponResult.getMessage());
                
                if (couponResult.isValid()) {
                    originalAmount = couponResult.getOriginalPrice();
                    finalAmount = couponResult.getFinalPrice();
                    discountAmount = couponResult.getDiscountAmount();
                    discountDetails = "Coupon applied: " + request.getCouponCode() + 
                                     ", Discount: " + discountAmount + 
                                     " " + request.getCurrency();
                    log.info("Coupon applied: {}, Original amount: {}, Discount: {}, Final amount: {}", 
                             request.getCouponCode(), originalAmount, discountAmount, finalAmount);
                } else {
                    log.warn("Invalid coupon code provided: {}, reason: {}", 
                             request.getCouponCode(), couponResult.getMessage());
                    throw new BadRequestException("Invalid coupon code: " + couponResult.getMessage());
                }
            }
        } else if (request.getAmount() != null) {
            // For non-subscription payments, use the provided amount
            finalAmount = request.getAmount();
            originalAmount = request.getOriginalAmount() != null ? request.getOriginalAmount() : finalAmount;
            
            // Calculate discount if original amount is provided
            if (request.getOriginalAmount() != null && request.getOriginalAmount().compareTo(finalAmount) > 0) {
                discountAmount = request.getOriginalAmount().subtract(finalAmount);
            }
        }
        
        try {
            String referenceId = generateReferenceId();
            String orderId = "order_" + UUID.randomUUID().toString().replace("-", "");
            
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
                    .discountDetails(discountDetails)
                    .build();
            
            paymentRepository.save(transaction);
            
            log.info("Payment transaction created: referenceId={}, amount={}, originalAmount={}", 
                    transaction.getReferenceId(), transaction.getAmount(), transaction.getOriginalAmount());
                    
            // Build response with checkout information
            PaymentResponse response = PaymentResponse.builder()
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
                    .expiresInMinutes(30) // Orders expire after 30 minutes
                    .discountDetails(discountDetails)
                    .couponCode(request.getCouponCode())
                    .build();
            
            log.info("Payment response details - finalAmount: {}, originalAmount: {}, discountAmount: {}", 
                    finalAmount, originalAmount, discountAmount);
                    
            log.info("Payment initiated with referenceId: {}, amount: {}, originalAmount: {}, discountDetails: {}", 
                    referenceId, finalAmount, originalAmount, discountDetails);
                    
            return response;
            
        } catch (Exception e) {
            log.error("Error initiating payment: {}", e.getMessage(), e);
            throw new PaymentException("Failed to initiate payment: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentVerificationResponse verifyPayment(PaymentVerificationRequest request) {
        log.info("Verifying payment for reference ID: {}", request.getReferenceId());
        
        PaymentTransaction transaction = getPaymentByReferenceId(request.getReferenceId());
        
        // In a real implementation, this would verify the payment with the gateway
        // For now, we'll just update the status to COMPLETED
        transaction.setStatus(PaymentStatus.COMPLETED);
        transaction.setGatewayTransactionId(request.getGatewayTransactionId());
        transaction.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(transaction);
        
        return PaymentVerificationResponse.builder()
                .success(true)
                .referenceId(transaction.getReferenceId())
                .gatewayOrderId(transaction.getGatewayOrderId())
                .gatewayTransactionId(transaction.getGatewayTransactionId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .status(transaction.getStatus())
                .completedAt(transaction.getCompletedAt())
                .build();
    }
    
    @Override
    public PaymentTransaction getPaymentByReferenceId(String referenceId) {
        return paymentRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found with reference ID: " + referenceId));
    }
    
    @Override
    public boolean cancelPayment(String referenceId) {
        PaymentTransaction transaction = getPaymentByReferenceId(referenceId);
        
        if (transaction.getStatus() == PaymentStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed payment");
        }
        
        transaction.setStatus(PaymentStatus.CANCELLED);
        transaction.setCancelledAt(LocalDateTime.now());
        paymentRepository.save(transaction);
        
        return true;
    }
    
    @Override
    public String checkPaymentStatus(String referenceId) {
        PaymentTransaction transaction = getPaymentByReferenceId(referenceId);
        return transaction.getStatus().name();
    }
    
    @Override
    public String generateReceipt(String referenceId) {
        PaymentTransaction transaction = getPaymentByReferenceId(referenceId);
        
        if (transaction.getStatus() != PaymentStatus.COMPLETED) {
            throw new BadRequestException("Cannot generate receipt for non-completed payment");
        }
        
        // In a real implementation, this would generate a PDF receipt
        String receiptUrl = "/api/payments/receipts/" + transaction.getReferenceId();
        transaction.setReceiptUrl(receiptUrl);
        paymentRepository.save(transaction);
        
        return receiptUrl;
    }
    
    @Override
    public String processRefund(String referenceId, Double amount, String reason) {
        PaymentTransaction transaction = getPaymentByReferenceId(referenceId);
        
        if (!transaction.canBeRefunded()) {
            throw new BadRequestException("Payment cannot be refunded");
        }
        
        // Calculate refund amount
        BigDecimal refundAmount = amount != null ? 
                BigDecimal.valueOf(amount) : transaction.getAmount();
                
        if (refundAmount.compareTo(transaction.getAmount()) > 0) {
            throw new BadRequestException("Refund amount cannot exceed payment amount");
        }
        
        // In a real implementation, this would call the payment gateway's refund API
        String refundReferenceId = "REF" + System.currentTimeMillis();
        
        transaction.setRefundReferenceId(refundReferenceId);
        transaction.setRefundAmount(refundAmount);
        transaction.setRefundReason(reason);
        transaction.setRefundStatus("COMPLETED");
        transaction.setStatus(refundAmount.compareTo(transaction.getAmount()) == 0 ? 
                PaymentStatus.REFUNDED : PaymentStatus.PARTIALLY_REFUNDED);
                
        paymentRepository.save(transaction);
        
        return refundReferenceId;
    }
    
    private String generateReferenceId() {
        return "RANPP" + 
               LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + 
               UUID.randomUUID().toString().substring(0, 8);
    }
} 