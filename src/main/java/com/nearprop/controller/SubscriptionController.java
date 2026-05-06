package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.CouponValidationResponseDto;
import com.nearprop.dto.CreateSubscriptionDto;
import com.nearprop.dto.InitiateSubscriptionPaymentDto;
import com.nearprop.dto.PropertyDto;
import com.nearprop.dto.SubscriptionDto;
import com.nearprop.dto.SubscriptionPlanDto;
import com.nearprop.dto.payment.InitiatePaymentRequest;
import com.nearprop.dto.payment.PaymentResponse;
import com.nearprop.entity.PaymentTransaction.PaymentType;
import com.nearprop.entity.Subscription;
import com.nearprop.entity.SubscriptionPlan;
import com.nearprop.entity.SubscriptionPlan.PlanType;
import com.nearprop.entity.User;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.repository.SubscriptionPlanRepository;
import com.nearprop.service.PaymentService;
import com.nearprop.service.PropertyService;
import com.nearprop.service.SubscriptionService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.math.BigDecimal;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final PropertyService propertyService;
    private final PaymentService paymentService;
    private final JavaMailSender mailSender;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @GetMapping("/health")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("application", "NearProp");
        health.put("version", "1.0.0");
        health.put("endpoint", "subscription-health");
        return ResponseEntity.ok(health);
    }

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<SubscriptionPlanDto>>> getSubscriptionPlans(
            @RequestParam(required = false) PlanType type) {
        log.info("REST request to get subscription plans for type: {}", type);
        
        List<SubscriptionPlanDto> plans = type != null 
                ? subscriptionService.getSubscriptionPlans(type)
                : subscriptionService.getAllSubscriptionPlans();
                
        return ResponseEntity.ok(ApiResponse.success("Subscription plans retrieved successfully", plans));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SubscriptionDto>> createSubscription(
            @Valid @RequestBody CreateSubscriptionDto createDto) {
        log.info("REST request to create subscription for plan: {}", createDto.getPlanId());
        
        SubscriptionDto result = subscriptionService.createSubscription(createDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subscription created successfully", result));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SubscriptionDto>> getSubscription(@PathVariable Long id) {
        log.info("REST request to get subscription: {}", id);
        
        SubscriptionDto result = subscriptionService.getSubscription(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription retrieved successfully", result));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<SubscriptionDto>>> getUserSubscriptions(Pageable pageable) {
        log.info("REST request to get user subscriptions");
        
        Page<SubscriptionDto> result = subscriptionService.getUserSubscriptions(pageable);
        return ResponseEntity.ok(ApiResponse.success("User subscriptions retrieved successfully", result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> cancelSubscription(@PathVariable Long id) {
        log.info("REST request to cancel subscription: {}", id);
        
        subscriptionService.cancelSubscription(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription cancelled successfully"));
    }

    @PostMapping("/{id}/renew")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SubscriptionDto>> renewSubscription(@PathVariable Long id) {
        log.info("REST request to renew subscription: {}", id);
        
        SubscriptionDto result = subscriptionService.renewSubscription(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription renewed successfully", result));
    }

    @GetMapping("/validate-coupon")
    public ResponseEntity<ApiResponse<CouponValidationResponseDto>> validateCoupon(
            @RequestParam Long planId,
            @RequestParam String couponCode) {
        log.info("REST request to validate coupon {} for plan: {}", couponCode, planId);
        
        CouponValidationResponseDto result = subscriptionService.applyCouponToSubscriptionPlan(planId, couponCode);
        return ResponseEntity.ok(ApiResponse.success("Coupon validation result", result));
    }

    /**
     * Check if the current user can add more properties based on subscription limits
     * 
     * @param currentUser The authenticated user
     * @return Response with information about property limits
     */
    @GetMapping("/can-add-property")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> canAddMoreProperties(@AuthenticationPrincipal User currentUser) {
        log.debug("Checking if user can add more properties: {}", currentUser != null ? currentUser.getId() : "null");
        
        if (currentUser == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("canAddProperty", false);
            errorResponse.put("availableSlots", 0);
            errorResponse.put("error", "Authentication required");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        
        try {
            Long userId = currentUser.getId();
            log.debug("User ID: {}", userId);
            
            boolean canAdd = subscriptionService.canAddMoreProperties(userId);
            log.debug("Can user add more properties: {}", canAdd);
            
            // Get the number of available slots
            long availableSlots = 0;
            
            // Check all plan types that can add properties
            Optional<Subscription> subscription = Optional.empty();
            PlanType[] planTypes = new PlanType[] {
                PlanType.PROPERTY, 
                PlanType.ADVISOR, 
                PlanType.SELLER, 
                PlanType.DEVELOPER, 
                PlanType.FRANCHISEE
            };
            
            // Try each plan type until we find one with available slots
            for (PlanType planType : planTypes) {
                Optional<Subscription> typedSubscription = subscriptionService.getSubscriptionWithAvailablePropertySlots(
                        userId, planType);
                if (typedSubscription.isPresent()) {
                    subscription = typedSubscription;
                    break;
                }
            }
            
            if (subscription.isPresent()) {
                SubscriptionPlan plan = subscription.get().getPlan();
                if (plan.getMaxProperties() != null && plan.getMaxProperties() > 0) {
                    long usedSlots = subscriptionService.countPropertiesBySubscriptionId(subscription.get().getId());
                    availableSlots = plan.getMaxProperties() - usedSlots;
                    log.debug("User has {} slots available out of {} with plan type {}", 
                            availableSlots, plan.getMaxProperties(), plan.getType());
                } else {
                    // Unlimited properties
                    availableSlots = -1;
                    log.debug("User has unlimited property slots with plan type {}", plan.getType());
                }
            } else {
                log.debug("User has no subscription with available property slots");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("canAddProperty", canAdd);
            response.put("availableSlots", availableSlots);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error checking if user can add more properties: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("canAddProperty", false);
            errorResponse.put("availableSlots", 0);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @Data
    public static class EmailRequest {
        private String to;
        private String subject;
        private String body;
    }
    
    @GetMapping(value = "/test-email", produces = MediaType.TEXT_HTML_VALUE)
    @PreAuthorize("permitAll()")
    public String getEmailTestForm() {
        return "<html><head><title>Email Test</title></head><body>" +
               "<h1>Email Test Form</h1>" +
               "<form id='emailForm'>" +
               "  <div><label>To: <input type='email' id='to' name='to' required></label></div>" +
               "  <div><label>Subject: <input type='text' id='subject' name='subject' required></label></div>" +
               "  <div><label>Body: <textarea id='body' name='body' rows='5' cols='40' required></textarea></label></div>" +
               "  <div><button type='button' onclick='sendEmail()'>Send Email</button></div>" +
               "</form>" +
               "<div id='result'></div>" +
               "<script>" +
               "function sendEmail() {" +
               "  const to = document.getElementById('to').value;" +
               "  const subject = document.getElementById('subject').value;" +
               "  const body = document.getElementById('body').value;" +
               "  " +
               "  fetch('/subscriptions/send-test-email', {" +
               "    method: 'POST'," +
               "    headers: {" +
               "      'Content-Type': 'application/json'" +
               "    }," +
               "    body: JSON.stringify({to, subject, body})" +
               "  })" +
               "  .then(response => response.json())" +
               "  .then(data => {" +
               "    document.getElementById('result').innerHTML = " +
               "      `<pre>${JSON.stringify(data, null, 2)}</pre>`;" +
               "  })" +
               "  .catch(error => {" +
               "    document.getElementById('result').innerHTML = " +
               "      `<pre>Error: ${error.message}</pre>`;" +
               "  });" +
               "}" +
               "</script>" +
               "</body></html>";
    }
    
    @PostMapping("/send-test-email")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> sendTestEmail(@RequestBody EmailRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("Attempting to send test email to: {}", request.getTo());
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(request.getBody(), true); // true indicates HTML content
            helper.setFrom("sandeep.acoreithub@gmail.com");
            
            mailSender.send(message);
            
            log.info("Test email sent successfully to: {}", request.getTo());
            
            response.put("success", true);
            response.put("message", "Email sent successfully");
            return ResponseEntity.ok(response);
        } catch (MessagingException e) {
            log.error("Failed to send test email: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            log.error("Unexpected error while sending email: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/payment/initiate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiateSubscriptionPayment(
            @Valid @RequestBody InitiateSubscriptionPaymentDto paymentDto) {
        log.info("REST request to initiate subscription payment for plan: {}", paymentDto.getPlanId());
        
        // Get subscription plan to get the price
        SubscriptionPlan plan = subscriptionPlanRepository.findById(paymentDto.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with ID: " + paymentDto.getPlanId()));
        
        BigDecimal planPrice = plan.getPrice();
        log.info("Plan price for ID {}: {}", paymentDto.getPlanId(), planPrice);
        
        // Create payment request
        InitiatePaymentRequest paymentRequest = InitiatePaymentRequest.builder()
                .paymentType(PaymentType.SUBSCRIPTION)
                .subscriptionPlanId(paymentDto.getPlanId())
                .autoRenew(paymentDto.getAutoRenew())
                .currency("INR") // Default currency
                .amount(planPrice) // Set the amount from plan price
                .originalAmount(planPrice) // Set original amount to plan price
                .couponCode(paymentDto.getCouponCode())
                .preferredPaymentMethod(paymentDto.getPreferredPaymentMethod())
                .build();
        
        PaymentResponse result = paymentService.initiatePayment(paymentRequest);
        
        // Fix discount amount if needed
        if (result.getDiscountAmount() == null || result.getDiscountAmount().compareTo(BigDecimal.ZERO) == 0) {
            // Check if we have a valid coupon and set discount amount explicitly
            if (paymentDto.getCouponCode() != null && !paymentDto.getCouponCode().isEmpty()) {
                CouponValidationResponseDto couponResult = subscriptionService.applyCouponToSubscriptionPlan(
                        paymentDto.getPlanId(), paymentDto.getCouponCode());
                
                if (couponResult.isValid()) {
                    log.info("Manually fixing discount amount: original={}, final={}, discount={}",
                            couponResult.getOriginalPrice(), couponResult.getFinalPrice(), couponResult.getDiscountAmount());
                    
                    result.setOriginalAmount(couponResult.getOriginalPrice());
                    result.setAmount(couponResult.getFinalPrice());
                    result.setDiscountAmount(couponResult.getDiscountAmount());
                    result.setDiscountDetails(String.format("Coupon applied: %s, Discount: %.2f %s",
                            paymentDto.getCouponCode(), couponResult.getDiscountAmount(), "INR"));
                }
            }
        }
        
        log.info("Payment response: referenceId={}, amount={}, originalAmount={}, discountAmount={}", 
                result.getReferenceId(), result.getAmount(), result.getOriginalAmount(), result.getDiscountAmount());
                
        return ResponseEntity.ok(ApiResponse.success("Payment initiated successfully", result));
    }

    @GetMapping("/my-subscriptions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<SubscriptionDto>>> getMySubscriptions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        log.info("REST request to get my subscriptions with page: {}, size: {}, sortBy: {}, direction: {}", page, size, sortBy, direction);
        
        Sort sort = Sort.by(direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubscriptionDto> result = subscriptionService.getUserSubscriptions(pageable);
        return ResponseEntity.ok(ApiResponse.success("My subscriptions retrieved successfully", result));
    }
} 