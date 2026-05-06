package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.payment.InitiatePaymentRequest;
import com.nearprop.dto.payment.PaymentResponse;
import com.nearprop.dto.payment.PaymentVerificationRequest;
import com.nearprop.dto.payment.PaymentVerificationResponse;
import com.nearprop.entity.PaymentTransaction;
import com.nearprop.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    
    @PostMapping("/initiate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request,
            HttpServletRequest servletRequest) {
        
        log.info("Payment initiation request received for amount: {} {}", 
                request.getAmount(), request.getCurrency());
        
        PaymentResponse response = paymentService.initiatePayment(request);
        
        return ResponseEntity.ok(
                ApiResponse.success("Payment initiated successfully", response));
    }
    
    @PostMapping("/verify")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentVerificationResponse>> verifyPayment(
            @Valid @RequestBody PaymentVerificationRequest request) {
        
        log.info("Payment verification request received for reference ID: {}", 
                request.getReferenceId());
        
        PaymentVerificationResponse response = paymentService.verifyPayment(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(
                    ApiResponse.success("Payment verified successfully", response));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Payment verification failed"));
        }
    }
    
    @GetMapping("/{referenceId}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> checkPaymentStatus(
            @PathVariable String referenceId) {
        
        log.info("Payment status check for reference ID: {}", referenceId);
        
        String status = paymentService.checkPaymentStatus(referenceId);
        
        return ResponseEntity.ok(
                ApiResponse.success("Payment status retrieved", status));
    }
    
    @PostMapping("/{referenceId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> cancelPayment(
            @PathVariable String referenceId) {
        
        log.info("Payment cancellation request for reference ID: {}", referenceId);
        
        boolean cancelled = paymentService.cancelPayment(referenceId);
        
        return ResponseEntity.ok(
                ApiResponse.success("Payment cancelled successfully"));
    }
    
    @GetMapping("/{referenceId}/receipt")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> getReceipt(
            @PathVariable String referenceId) {
        
        log.info("Receipt request for reference ID: {}", referenceId);
        
        String receiptUrl = paymentService.generateReceipt(referenceId);
        
        return ResponseEntity.ok(
                ApiResponse.success("Receipt generated successfully", receiptUrl));
    }
    
    @PostMapping("/{referenceId}/refund")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> processRefund(
            @PathVariable String referenceId,
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) String reason) {
        
        log.info("Refund request for reference ID: {}, amount: {}", referenceId, amount);
        
        String refundId = paymentService.processRefund(referenceId, amount, reason);
        
        return ResponseEntity.ok(
                ApiResponse.success("Refund processed successfully", refundId));
    }
    
    // Admin endpoint to get payment transaction details
    @GetMapping("/admin/{referenceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentTransaction>> getPaymentDetails(
            @PathVariable String referenceId) {
        
        log.info("Admin payment details request for reference ID: {}", referenceId);
        
        PaymentTransaction transaction = paymentService.getPaymentByReferenceId(referenceId);
        
        return ResponseEntity.ok(
                ApiResponse.success("Payment details retrieved successfully", transaction));
    }
} 