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
import com.nearprop.repository.UserRepository;
import com.nearprop.service.PaymentService;
import com.nearprop.service.UserService;
import com.nearprop.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of PaymentService that uses Razorpay as the payment gateway
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RazorpayPaymentServiceImpl implements PaymentService {

    private final PaymentTransactionRepository paymentRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RestTemplate restTemplate;
    
    @Value("${payment.gateway.razorpay.key}")
    private String razorpayKey;
    
    @Value("${payment.gateway.razorpay.secret}")
    private String razorpaySecret;
    
    @Value("${payment.gateway.razorpay.api.url}")
    private String razorpayApiUrl;
    
    @Override
    @Transactional
    public PaymentResponse initiatePayment(InitiatePaymentRequest request) {
        log.info("Initiating payment for amount: {} {}", request.getAmount(), request.getCurrency());
        
        User currentUser = userService.getCurrentUser();
        
        try {
            String referenceId = generateReferenceId();
            
            // Create order in Razorpay
            HttpHeaders headers = createAuthHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            JSONObject orderRequest = new JSONObject();
            // Convert to smallest currency unit (paise for INR)
            int amountInSmallestUnit = request.getAmount().multiply(new BigDecimal(100)).intValue();
            
            orderRequest.put("amount", amountInSmallestUnit);
            orderRequest.put("currency", request.getCurrency());
            orderRequest.put("receipt", "rcpt_" + referenceId);
            
            // Add notes for our reference
            JSONObject notes = new JSONObject();
            notes.put("reference_id", referenceId);
            notes.put("user_id", currentUser.getId().toString());
            notes.put("payment_type", request.getPaymentType().toString());
            if (request.getSubscriptionPlanId() != null) {
                notes.put("subscription_plan_id", request.getSubscriptionPlanId().toString());
            }
            orderRequest.put("notes", notes);
            
            HttpEntity<String> entity = new HttpEntity<>(orderRequest.toString(), headers);
            
            // Call Razorpay API to create order
            String response = restTemplate.postForObject(
                    razorpayApiUrl + "/orders", 
                    entity, 
                    String.class
            );
            
            JSONObject jsonResponse = new JSONObject(response);
            String orderId = jsonResponse.getString("id");
            
            // Create payment transaction record
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .referenceId(referenceId)
                    .gatewayOrderId(orderId)
                    .user(currentUser)
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .paymentType(request.getPaymentType())
                    .status(PaymentStatus.INITIATED)
                    .subscriptionId(request.getSubscriptionPlanId())
                    .build();
            
            paymentRepository.save(transaction);
            
            // Build response with checkout information
            return PaymentResponse.builder()
                    .referenceId(referenceId)
                    .gatewayOrderId(orderId)
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .status(PaymentStatus.INITIATED)
                    .createdAt(LocalDateTime.now())
                    .message("Payment initiated successfully")
                    .paymentToken(orderId) // Used by frontend to initiate checkout
                    .expiresInMinutes(30) // Razorpay orders expire after 30 minutes
                    .build();
            
        } catch (Exception e) {
            log.error("Error initiating payment: {}", e.getMessage(), e);
            throw new PaymentException("Failed to initiate payment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentVerificationResponse verifyPayment(PaymentVerificationRequest request) {
        log.info("Verifying payment for reference ID: {}", request.getReferenceId());
        
        PaymentTransaction transaction = paymentRepository.findByReferenceId(request.getReferenceId())
                .orElseThrow(() -> new PaymentException("Payment transaction not found"));
        
        try {
            // Verify signature to confirm payment authenticity
            String expectedSignature = calculateRazorpaySignature(
                    request.getGatewayOrderId(),
                    request.getGatewayTransactionId()
            );
            
            if (!expectedSignature.equals(request.getPaymentSignature())) {
                transaction.setStatus(PaymentStatus.FAILED);
                transaction.setFailureMessage("Signature verification failed");
                paymentRepository.save(transaction);
                
                return PaymentVerificationResponse.builder()
                        .success(false)
                        .referenceId(transaction.getReferenceId())
                        .status(PaymentStatus.FAILED)
                        .errorMessage("Payment verification failed: Invalid signature")
                        .build();
            }
            
            // Update transaction details
            transaction.setGatewayTransactionId(request.getGatewayTransactionId());
            transaction.setStatus(PaymentStatus.COMPLETED);
            transaction.setPaymentDate(LocalDateTime.now());
            
            // Get payment details from Razorpay
            HttpHeaders headers = createAuthHeaders();
            String paymentDetailsUrl = razorpayApiUrl + "/payments/" + request.getGatewayTransactionId();
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String response = restTemplate.getForObject(paymentDetailsUrl, String.class, entity);
            
            JSONObject jsonResponse = new JSONObject(response);
            
            // Update payment method details
            String method = jsonResponse.getString("method");
            transaction.setPaymentMethod(mapPaymentMethod(method));
            
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
                    .paymentMethod(method)
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
        
        if (!transaction.canBeRefunded()) {
            throw new PaymentException("Payment cannot be refunded");
        }
        
        try {
            HttpHeaders headers = createAuthHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            JSONObject refundRequest = new JSONObject();
            
            if (amount != null) {
                // Convert to smallest currency unit (paise for INR)
                int refundAmountInSmallestUnit = new BigDecimal(amount).multiply(new BigDecimal(100)).intValue();
                refundRequest.put("amount", refundAmountInSmallestUnit);
                transaction.setRefundAmount(new BigDecimal(amount));
            } else {
                // Full refund
                transaction.setRefundAmount(transaction.getAmount());
            }
            
            refundRequest.put("speed", "normal");
            
            if (reason != null) {
                refundRequest.put("notes", new JSONObject().put("reason", reason));
                transaction.setRefundReason(reason);
            }
            
            HttpEntity<String> entity = new HttpEntity<>(refundRequest.toString(), headers);
            
            // Call Razorpay API to process refund
            String refundUrl = razorpayApiUrl + "/payments/" + transaction.getGatewayTransactionId() + "/refund";
            String response = restTemplate.postForObject(refundUrl, entity, String.class);
            
            JSONObject jsonResponse = new JSONObject(response);
            String refundId = jsonResponse.getString("id");
            
            // Update transaction
            transaction.setRefundReferenceId(refundId);
            transaction.setStatus(transaction.getRefundAmount().compareTo(transaction.getAmount()) == 0 ? 
                    PaymentStatus.REFUNDED : PaymentStatus.PARTIALLY_REFUNDED);
            transaction.setRefundStatus("PROCESSED");
            
            paymentRepository.save(transaction);
            
            return refundId;
        } catch (Exception e) {
            log.error("Error processing refund: {}", e.getMessage(), e);
            throw new PaymentException("Refund processing failed: " + e.getMessage());
        }
    }
    
    /**
     * Clean up stale payment transactions that were initiated but not completed
     */
    @Scheduled(cron = "0 0 * * * *") // Run every hour
    @Transactional
    public void cleanupStalePayments() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(1); // 1 hour old
        List<PaymentTransaction> stalePayments = paymentRepository.findStalePaymentsByStatus(
                PaymentStatus.INITIATED, cutoffTime);
        
        log.info("Found {} stale payment transactions to clean up", stalePayments.size());
        
        for (PaymentTransaction payment : stalePayments) {
            payment.setStatus(PaymentStatus.CANCELLED);
            payment.setFailureMessage("Payment timed out");
            payment.setFailureCode("TIMEOUT");
        }
        
        paymentRepository.saveAll(stalePayments);
    }
    
    // Helper methods
    
    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = razorpayKey + ":" + razorpaySecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.add("Authorization", "Basic " + encodedAuth);
        return headers;
    }
    
    private String generateReferenceId() {
        return IdGenerator.generatePaymentId();
    }
    
    private String calculateRazorpaySignature(String orderId, String paymentId) throws NoSuchAlgorithmException, InvalidKeyException {
        String data = orderId + "|" + paymentId;
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(razorpaySecret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secretKey);
        return bytesToHex(sha256_HMAC.doFinal(data.getBytes()));
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    private PaymentTransaction.PaymentMethod mapPaymentMethod(String razorpayMethod) {
        switch (razorpayMethod.toLowerCase()) {
            case "card":
                return PaymentTransaction.PaymentMethod.CREDIT_CARD;
            case "netbanking":
                return PaymentTransaction.PaymentMethod.NET_BANKING;
            case "upi":
                return PaymentTransaction.PaymentMethod.UPI;
            case "wallet":
                return PaymentTransaction.PaymentMethod.WALLET;
            case "emi":
                return PaymentTransaction.PaymentMethod.EMI;
            default:
                return PaymentTransaction.PaymentMethod.OTHER;
        }
    }
} 