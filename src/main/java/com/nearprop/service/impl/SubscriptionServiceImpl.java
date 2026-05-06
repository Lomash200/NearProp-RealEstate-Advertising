package com.nearprop.service.impl;

import com.nearprop.dto.CouponValidationDto;
import com.nearprop.dto.CouponValidationResponseDto;
import com.nearprop.dto.CreateSubscriptionDto;
import com.nearprop.dto.PropertyDto;
import com.nearprop.dto.SubscriptionDto;
import com.nearprop.dto.SubscriptionPlanDto;
import com.nearprop.dto.UserSummaryDto;
import com.nearprop.entity.*;
import com.nearprop.entity.Subscription.SubscriptionStatus;
import com.nearprop.entity.SubscriptionPlan.PlanType;
import com.nearprop.exception.BadRequestException;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.exception.UnauthorizedException;
import com.nearprop.repository.CouponRepository;
import com.nearprop.repository.PropertyRepository;
import com.nearprop.repository.ReelRepository;
import com.nearprop.repository.SubscriptionPlanRepository;
import com.nearprop.repository.SubscriptionRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.service.CouponService;
import com.nearprop.service.SubscriptionEmailService;
import com.nearprop.service.SubscriptionService;
import com.nearprop.service.UserService;
import com.nearprop.service.PropertyEmailService;
import com.nearprop.service.franchisee.DistrictRevenueService;
import com.nearprop.entity.DistrictRevenue.RevenueType;
import com.nearprop.entity.DistrictRevenue.PaymentStatus;
import com.nearprop.repository.franchisee.FranchiseeDistrictRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.nearprop.entity.SubscriptionPlan.PlanType;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final ReelRepository reelRepository;
    private final UserService userService;
    private final SubscriptionEmailService subscriptionEmailService;
    private final PropertyEmailService propertyEmailService;
    private final CouponRepository couponRepository;
    private final CouponService couponService;
    private final DistrictRevenueService districtRevenueService;
    private final FranchiseeDistrictRepository franchiseeDistrictRepository;

    @Override
    public List<SubscriptionPlanDto> getSubscriptionPlans(PlanType type) {
        return subscriptionPlanRepository.findByTypeAndActiveTrue(type).stream()
                .map(this::mapToPlanDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SubscriptionPlanDto> getAllSubscriptionPlans() {
        return subscriptionPlanRepository.findByActiveTrue().stream()
                .map(this::mapToPlanDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public SubscriptionDto createSubscription(CreateSubscriptionDto createDto) {
        User currentUser = userService.getCurrentUser();
        
        SubscriptionPlan plan = subscriptionPlanRepository.findById(createDto.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with ID: " + createDto.getPlanId()));
        
        // Check if the plan is active
        if (!plan.isActive()) {
            throw new BadRequestException("Selected subscription plan is not available");
        }
        
        // Handle renewal case
        boolean isRenewal = createDto.getIsRenewal() != null && createDto.getIsRenewal();
        
        // Calculate final price with coupon if provided
        BigDecimal finalPrice = plan.getPrice();
        String appliedCouponCode = null;
        
        if (createDto.getCouponCode() != null && !createDto.getCouponCode().isEmpty()) {
            CouponValidationDto validationDto = new CouponValidationDto();
            validationDto.setCode(createDto.getCouponCode());
            validationDto.setPlanId(createDto.getPlanId());
            
            CouponValidationResponseDto couponResult = couponService.validateCoupon(validationDto);
            
            if (couponResult.isValid()) {
                finalPrice = couponResult.getFinalPrice();
                appliedCouponCode = createDto.getCouponCode();
                log.info("Coupon applied: {}, Original price: {}, Final price: {}", 
                         createDto.getCouponCode(), plan.getPrice(), finalPrice);
            } else {
                log.warn("Invalid coupon code ignored: {}", createDto.getCouponCode());
            }
        }
        
        // Create new subscription
        Subscription subscription = Subscription.builder()
                .user(currentUser)
                .plan(plan)
                .price(finalPrice)
                .originalPrice(plan.getPrice())
                .isRenewal(isRenewal)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(plan.getDurationDays()))
                .status(SubscriptionStatus.ACTIVE)
                .autoRenew(createDto.getAutoRenew() != null ? createDto.getAutoRenew() : false)
                .paymentReference(createDto.getPaymentReferenceId() != null ? 
                        createDto.getPaymentReferenceId() : "PAYMENT-" + System.currentTimeMillis())
                .couponCode(appliedCouponCode)
                .districtId(createDto.getDistrictId())
                .build();
        
        // For franchise plans, add marketing fee only for new subscriptions (not renewals)
        if (plan.getType() == PlanType.FRANCHISEE && plan.getMarketingFee() != null && !isRenewal) {
            subscription.setMarketingFee(plan.getMarketingFee());
        }
        
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        log.info("Created subscription with ID: {} for user: {}, district: {}", 
            savedSubscription.getId(), currentUser.getId(), createDto.getDistrictId());
        
        // Find the franchisee district for revenue recording
        List<com.nearprop.entity.FranchiseeDistrict> franchiseeDistricts = 
            franchiseeDistrictRepository.findByDistrictId(createDto.getDistrictId());
        
        if (franchiseeDistricts.isEmpty()) {
            throw new ResourceNotFoundException("No franchisee found for district ID: " + createDto.getDistrictId());
        }
        
        com.nearprop.entity.FranchiseeDistrict franchiseeDistrict = franchiseeDistricts.get(0);
        
        // Record the revenue with the discounted amount
        boolean isPaid = createDto.getPaymentReferenceId() != null && !createDto.getPaymentReferenceId().isEmpty();
        
        districtRevenueService.recordRevenue(
            franchiseeDistrict.getDistrictId(),
            franchiseeDistrict.getId(),
            RevenueType.SUBSCRIPTION_PAYMENT,
            finalPrice,
            String.format("Subscription payment for user %d (Plan: %s%s)", 
                currentUser.getId(), 
                plan.getName(),
                appliedCouponCode != null ? ", Coupon: " + appliedCouponCode : ""),
            isPaid ? PaymentStatus.PAID : PaymentStatus.PENDING,
            savedSubscription.getId()
        );
        
        log.info("Revenue recorded for district {} - Amount: {}, Status: {}, Original: {}, Discount: {}", 
            createDto.getDistrictId(), finalPrice, isPaid ? "PAID" : "PENDING", plan.getPrice(), 
            appliedCouponCode != null ? plan.getPrice().subtract(finalPrice) : BigDecimal.ZERO);
        
        // Send subscription creation email notification
        try {
            subscriptionEmailService.sendSubscriptionCreatedNotification(savedSubscription);
            log.info("Subscription creation notification email sent for subscription ID: {}", savedSubscription.getId());
        } catch (Exception e) {
            log.error("Failed to send subscription creation notification email: {}", e.getMessage(), e);
        }
        
        // Activate user's inactive properties if this is a property or advisor subscription
        if (plan.getType() == PlanType.PROPERTY || plan.getType() == PlanType.ADVISOR || 
            plan.getType() == PlanType.SELLER || plan.getType() == PlanType.DEVELOPER || 
            plan.getType() == PlanType.FRANCHISEE) {
            
            List<Property> activatedProperties = activateUserProperties(currentUser.getId(), savedSubscription);
            log.info("Activated {} properties for user {} with subscription {}", 
                activatedProperties.size(), currentUser.getId(), savedSubscription.getId());
            
            // Send email notification for activated properties
            if (!activatedProperties.isEmpty()) {
                try {
                    subscriptionEmailService.sendSubscriptionCreatedNotification(savedSubscription, activatedProperties);
                    log.info("Property activation notification email sent for {} properties", activatedProperties.size());
                } catch (Exception e) {
                    log.error("Failed to send property activation notification email: {}", e.getMessage(), e);
                }
            }
        }
        
        return mapToDto(savedSubscription);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SubscriptionDto getSubscription(Long subscriptionId) {
        User currentUser = userService.getCurrentUser();
        Subscription subscription = findSubscriptionById(subscriptionId);
        
        // Only the subscription owner or an admin can view the subscription
        if (!subscription.getUser().getId().equals(currentUser.getId()) && !currentUser.getRoles().contains("ADMIN")) {
            throw new UnauthorizedException("You are not authorized to view this subscription");
        }
        
        return mapToDto(subscription);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SubscriptionDto> getUserSubscriptions(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return subscriptionRepository.findByUserId(currentUser.getId(), pageable)
                .map(this::mapToDto);
    }
    
    @Override
    @Transactional
    public void cancelSubscription(Long subscriptionId) {
        User currentUser = userService.getCurrentUser();
        Subscription subscription = findSubscriptionById(subscriptionId);
        
        // Only the subscription owner or an admin can cancel the subscription
        if (!subscription.getUser().getId().equals(currentUser.getId()) && !currentUser.getRoles().contains("ADMIN")) {
            throw new UnauthorizedException("You are not authorized to cancel this subscription");
        }
        
        // Set subscription status to CANCELLED
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setCancelledAt(LocalDateTime.now());
        subscription.setAutoRenew(false); // Ensure auto-renewal is turned off
        
        subscriptionRepository.save(subscription);
        
        // Immediately deactivate properties for property and advisor subscriptions
        if (subscription.getPlan().getType() == PlanType.PROPERTY || subscription.getPlan().getType() == PlanType.ADVISOR) {
            List<Property> deactivatedProperties = deactivatePropertiesBySubscription(subscription);
            
            // Send email notification
            if (!deactivatedProperties.isEmpty()) {
                subscriptionEmailService.sendSubscriptionCancelledNotification(subscription, deactivatedProperties);
            }
        }
    }
    
    @Override
    @Transactional
    public SubscriptionDto renewSubscription(Long subscriptionId) {
        User currentUser = userService.getCurrentUser();
        Subscription subscription = findSubscriptionById(subscriptionId);
        
        // Only the subscription owner or an admin can renew the subscription
        if (!subscription.getUser().getId().equals(currentUser.getId()) && !currentUser.getRoles().contains("ADMIN")) {
            throw new UnauthorizedException("You are not authorized to renew this subscription");
        }
        
        // Only allow renewal of ACTIVE or EXPIRED subscriptions
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE && subscription.getStatus() != SubscriptionStatus.EXPIRED) {
            throw new BadRequestException("Only active or expired subscriptions can be renewed");
        }
        
        // Create a new subscription with the same plan
        LocalDateTime newStartDate = subscription.getEndDate().isAfter(LocalDateTime.now())
                ? subscription.getEndDate()  // Start after current subscription ends
                : LocalDateTime.now();       // Start now if already expired
                
        Subscription newSubscription = Subscription.builder()
                .user(subscription.getUser())
                .plan(subscription.getPlan())
                .price(subscription.getPlan().getPrice())
                .startDate(newStartDate)
                .endDate(newStartDate.plusDays(subscription.getPlan().getDurationDays()))
                .status(SubscriptionStatus.ACTIVE)
                .autoRenew(subscription.isAutoRenew())
                // In a real implementation, this would involve a payment gateway integration
                .paymentReference("RENEWAL-" + System.currentTimeMillis())
                .build();
                
        Subscription savedSubscription = subscriptionRepository.save(newSubscription);
        
        // If current subscription is still active, cancel it
        if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
            subscription.setStatus(SubscriptionStatus.CANCELLED);
            subscription.setCancelledAt(LocalDateTime.now());
            subscriptionRepository.save(subscription);
        }
        
        // Reactivate user's inactive properties if this is a property or advisor subscription
        if (subscription.getPlan().getType() == PlanType.PROPERTY || subscription.getPlan().getType() == PlanType.ADVISOR) {
            List<Property> reactivatedProperties = activateUserProperties(currentUser.getId(), savedSubscription);
            
            // Send email notification
            if (!reactivatedProperties.isEmpty()) {
                subscriptionEmailService.sendSubscriptionRenewedNotification(savedSubscription, reactivatedProperties);
            }
        }
        
        return mapToDto(savedSubscription);
    }
    
    @Override
    public boolean hasActiveSubscription(Long userId, PlanType planType) {
        return subscriptionRepository.countActiveSubscriptionsByUserIdAndPlanType(userId, planType) > 0;
    }
    
    @Override
    @Scheduled(cron = "0 0 * * * *") // Every hour
    @Transactional
    public void processExpiredSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        log.info("=== SCHEDULER: processExpiredSubscriptions() STARTED at {} ===", now);
        // Find expired active subscriptions
        List<Subscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions(SubscriptionStatus.ACTIVE, now);
        log.info("Processing {} expired subscriptions", expiredSubscriptions.size());
        
        for (Subscription subscription : expiredSubscriptions) {
            // Update subscription status to EXPIRED
            log.info("Expiring subscription ID: {} | User: {} | EndDate: {}",
                    subscription.getId(),
                    subscription.getUser().getId(),
                    subscription.getEndDate());            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(subscription);
            
            log.info("Marked subscription {} as EXPIRED", subscription.getId());
            
            // Deactivate properties for property and advisor subscriptions
            if (subscription.getPlan().getType() == PlanType.PROPERTY || subscription.getPlan().getType() == PlanType.ADVISOR) {
                List<Property> deactivatedProperties = deactivatePropertiesBySubscription(subscription);
                
                // Send email notification
                if (!deactivatedProperties.isEmpty()) {
                    subscriptionEmailService.sendSubscriptionExpiredNotification(subscription, deactivatedProperties);
                }
            }
        }
        
        // Process content visibility (hiding/deletion)
        processContentVisibility();
        
        // Process auto-renewals
        processAutoRenewals();
        
        // Send expiration warning emails for subscriptions expiring soon
        sendExpirationWarnings();
    }
    
    @Transactional
    private void processContentVisibility() {
        LocalDateTime now = LocalDateTime.now();
        
        // Find subscriptions that need content hidden
        List<Subscription> subscriptionsToHide = subscriptionRepository.findSubscriptionsForContentHiding(now);
        log.info("Processing {} subscriptions for content hiding", subscriptionsToHide.size());
        
        for (Subscription subscription : subscriptionsToHide) {
            // Skip franchise subscriptions - content is always visible
            if (subscription.getPlan().isFranchisePlan()) {
                continue;
            }
            
            log.info("Hiding content for subscription {}", subscription.getId());
            
            // Update subscription status
            subscription.setStatus(SubscriptionStatus.CONTENT_HIDDEN);
            subscription.setContentHiddenAt(now);
            subscriptionRepository.save(subscription);
            
            // For user-wide subscription, find all properties owned by user
            Page<Property> properties = propertyRepository.findByOwnerId(subscription.getUser().getId(), Pageable.unpaged());
            for (Property property : properties) {
                property.setActive(false);
                propertyRepository.save(property);
                
                // Hide all reels for each property
                Page<Reel> reels = reelRepository.findByPropertyId(property.getId(), Pageable.unpaged());
                for (Reel reel : reels) {
                    reel.setStatus(Reel.ReelStatus.HIDDEN);
                    reelRepository.save(reel);
                }
            }
        }
        
        // Find subscriptions that need content deleted
        List<Subscription> subscriptionsToDelete = subscriptionRepository.findSubscriptionsForContentDeletion(now);
        log.info("Processing {} subscriptions for content deletion", subscriptionsToDelete.size());
        
        for (Subscription subscription : subscriptionsToDelete) {
            // Skip franchise subscriptions - content is never deleted
            if (subscription.getPlan().isFranchisePlan()) {
                continue;
            }
            
            log.info("Deleting content for subscription {}", subscription.getId());
            
            // Update subscription status
            subscription.setStatus(SubscriptionStatus.CONTENT_DELETED);
            subscription.setContentDeletedAt(now);
            subscriptionRepository.save(subscription);
            
            // For user-wide subscription, find all properties owned by user
            Page<Property> properties = propertyRepository.findByOwnerId(subscription.getUser().getId(), Pageable.unpaged());
            for (Property property : properties) {
                // Delete all reels for each property first
                Page<Reel> reels = reelRepository.findByPropertyId(property.getId(), Pageable.unpaged());
                for (Reel reel : reels) {
                    reelRepository.delete(reel);
                }
                
                // Then delete the property
                propertyRepository.delete(property);
            }
        }
    }
    
    @Override
    public long getSubscriptionCount(Long userId, PlanType planType) {
        return subscriptionRepository.countByUserIdAndPlanType(userId, planType);
    }
    
    @Override
    public LocalDateTime getPropertySubscriptionExpiryDate(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        
        // Check for PROPERTY subscriptions
        List<Subscription> propertySubscriptions = subscriptionRepository
                .findActiveByUserIdAndPlanType(userId, PlanType.PROPERTY, now);
        
        // Check for ADVISOR subscriptions if no PROPERTY subscriptions found
        if (propertySubscriptions.isEmpty()) {
            propertySubscriptions = subscriptionRepository
                    .findActiveByUserIdAndPlanType(userId, PlanType.ADVISOR, now);
        }
        
        if (propertySubscriptions.isEmpty()) {
            return null;
        }
        
        // Find the subscription with the latest expiry date
        return propertySubscriptions.stream()
                .map(Subscription::getEndDate)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }
    
    @Override
    public boolean canAddMoreProperties(Long userId) {
        // Check for all plan types that can add properties
        for (PlanType planType : new PlanType[] {
                PlanType.PROPERTY, 
                PlanType.ADVISOR, 
                PlanType.SELLER, 
                PlanType.DEVELOPER, 
                PlanType.FRANCHISEE
            }) {
            Optional<Subscription> subscription = getSubscriptionWithAvailablePropertySlots(userId, planType);
            if (subscription.isPresent()) {
                log.info("User {} can add properties with {} subscription ID: {}", 
                        userId, planType, subscription.get().getId());
                return true;
            }
        }
        
        log.info("User {} has no subscription with available property slots", userId);
        return false;
    }
    
   /* @Override
    public Optional<Subscription> getSubscriptionWithAvailablePropertySlots(Long userId, PlanType planType) {
        // First check for the requested plan type
        Optional<Subscription> subscription = subscriptionRepository.findActiveSubscriptionWithAvailablePropertySlots(userId, planType);
        
        // If subscription is found, return it
        if (subscription.isPresent()) {
            log.info("Found active subscription of type {} for user {} with available slots", planType, userId);
            return subscription;
        }
        
        // If not found, don't try other plan types - the caller will handle that
        log.info("No active subscription of type {} found for user {} with available slots", planType, userId);
        return Optional.empty();
    }*/

    @Override
    public Optional<Subscription> getSubscriptionWithAvailablePropertySlots(Long userId, PlanType planType) {
        // Fetch all active subscriptions with available slots, ordered by latest endDate first
        List<Subscription> subs = subscriptionRepository
                .findActiveSubscriptionsWithAvailablePropertySlots(userId, planType);

        if (!subs.isEmpty()) {
            Subscription chosen = subs.get(0); // pick the latest one
            log.info("Found active subscription of type {} for user {} with available slots (subscriptionId={})",
                     planType, userId, chosen.getId());
            return Optional.of(chosen);
        }

        log.info("No active subscription of type {} found for user {} with available slots", planType, userId);
        return Optional.empty();
    }
    
    @Override
    public long countPropertiesBySubscriptionId(Long subscriptionId) {
        return subscriptionRepository.countPropertiesBySubscriptionId(subscriptionId);
    }
    
    @Override
    public CouponValidationResponseDto applyCouponToSubscriptionPlan(Long planId, String couponCode) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            throw new BadRequestException("Coupon code cannot be empty");
        }
        
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with ID: " + planId));
        
        CouponValidationDto validationDto = new CouponValidationDto();
        validationDto.setCode(couponCode.trim().toUpperCase());
        validationDto.setOrderAmount(plan.getPrice());
        validationDto.setPlanId(planId);
        
        log.info("Validating coupon {} for plan ID: {}, type: {}, price: {}", 
                couponCode, planId, plan.getType(), plan.getPrice());
        
        return couponService.validateCoupon(validationDto);
    }
    
    // Helper methods
    
    private Subscription findSubscriptionById(Long subscriptionId) {
        return subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));
    }
    
    private SubscriptionPlanDto mapToPlanDto(SubscriptionPlan plan) {
        return SubscriptionPlanDto.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .type(plan.getType())
                .price(plan.getPrice())
                .durationDays(plan.getDurationDays())
                .maxProperties(plan.getMaxProperties())
                .maxReelsPerProperty(plan.getMaxReelsPerProperty())
                .maxTotalReels(plan.getMaxTotalReels())
                .active(plan.isActive())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }
    
    private SubscriptionDto mapToDto(Subscription subscription) {
        boolean isActive = subscription.isActive();
        boolean isInGracePeriod = subscription.isInGracePeriod();
        long daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), subscription.getEndDate());
        boolean contentVisible = isActive || isInGracePeriod;
                
        SubscriptionDto dto = SubscriptionDto.builder()
                .id(subscription.getId())
                .user(UserSummaryDto.builder()
                        .id(subscription.getUser().getId())
                        .name(subscription.getUser().getName())
                        .email(subscription.getUser().getEmail())
                        .phone(subscription.getUser().getMobileNumber())
                        .permanentId(subscription.getUser().getPermanentId())
                        .profileImageUrl(subscription.getUser().getProfileImageUrl())
                        .roles(subscription.getUser().getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet()))
                        .build())
                .plan(mapToPlanDto(subscription.getPlan()))
                .price(subscription.getPrice())
                .marketingFee(subscription.getMarketingFee())
                .totalAmount(subscription.getTotalAmount())
                .isRenewal(subscription.isRenewal())
                .previousSubscriptionId(subscription.getPreviousSubscriptionId())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .paymentReference(subscription.getPaymentReference())
                .autoRenew(subscription.isAutoRenew())
                .cancelledAt(subscription.getCancelledAt())
                .contentHiddenAt(subscription.getContentHiddenAt())
                .contentDeletedAt(subscription.getContentDeletedAt())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .isActive(isActive)
                .isInGracePeriod(isInGracePeriod)
                .daysRemaining((int) daysRemaining)
                .contentVisible(contentVisible)
                .build();
                
        return dto;
    }
    
    @Transactional
    private void processAutoRenewals() {
        LocalDateTime now = LocalDateTime.now();
        
        // Find expired subscriptions with auto-renew enabled
        List<Subscription> subscriptionsToRenew = subscriptionRepository.findExpiredSubscriptions(SubscriptionStatus.EXPIRED, now).stream()
                .filter(Subscription::isAutoRenew)
                .collect(Collectors.toList());
        
        log.info("Processing {} auto-renewals", subscriptionsToRenew.size());
        
        for (Subscription subscription : subscriptionsToRenew) {
            try {
                // In a real implementation, this would involve a payment gateway integration
                log.info("Auto-renewing subscription {} for user {}", subscription.getId(), subscription.getUser().getId());
                
                // Create a renewal DTO
                CreateSubscriptionDto renewalDto = CreateSubscriptionDto.builder()
                        .planId(subscription.getPlan().getId())
                        .autoRenew(true)
                        .isRenewal(true)
                        .previousSubscriptionId(subscription.getId())
                        .build();
                
                // Save user ID before creating new subscription
                Long userId = subscription.getUser().getId();
                
                // Create new subscription
                SubscriptionDto newSubscription = createSubscription(renewalDto);
                
                // Mark the old subscription as cancelled (since a new one has been created)
                subscription.setStatus(SubscriptionStatus.CANCELLED);
                subscription.setCancelledAt(now);
                subscriptionRepository.save(subscription);
                
                log.info("Successfully auto-renewed subscription {} for user {}", 
                    newSubscription.getId(), userId);
            } catch (Exception e) {
                log.error("Error during auto-renewal of subscription {}: {}", subscription.getId(), e.getMessage());
                // Auto-renewal failed, subscription remains EXPIRED
            }
        }
    }
    
    /**
     * Send warning emails for subscriptions that will expire soon
     */
    @Transactional(readOnly = true)
    private void sendExpirationWarnings() {
        LocalDateTime now = LocalDateTime.now();
        
        // Send warning 7 days before expiration
        LocalDateTime sevenDayWarning = now.plusDays(7);
        List<Subscription> subscriptionsExpiringSoon = subscriptionRepository
            .findByStatusAndEndDateBetween(SubscriptionStatus.ACTIVE, now, sevenDayWarning);
        
        log.info("Sending 7-day expiration warnings for {} subscriptions", subscriptionsExpiringSoon.size());
        
        for (Subscription subscription : subscriptionsExpiringSoon) {
            int daysUntilExpiry = (int) ChronoUnit.DAYS.between(now, subscription.getEndDate());
            if (daysUntilExpiry == 7 || daysUntilExpiry == 3 || daysUntilExpiry == 1) {
                // Send warning email
                subscriptionEmailService.sendSubscriptionExpiryWarningNotification(subscription, daysUntilExpiry);
            }
        }
    }
    
    /**
     * Activate properties for a user based on a subscription
     * 
     * @param userId User ID
     * @param subscription Subscription to use for activation
     * @return List of activated properties
     */
    @Transactional
    private List<Property> activateUserProperties(Long userId, Subscription subscription) {
        // Get inactive properties for the user
        List<Property> inactiveProperties = propertyRepository.findByOwnerIdAndActiveFalse(userId);
        log.info("Found {} inactive properties for user {}", inactiveProperties.size(), userId);
        
        if (inactiveProperties.isEmpty()) {
            return List.of();
        }
        
        // Check subscription limits
        Integer maxProperties = subscription.getPlan().getMaxProperties();
        int activationLimit = maxProperties == null || maxProperties < 0 ? 
            inactiveProperties.size() : maxProperties;
            
        // Get count of already active properties with this subscription
        long activePropertiesCount = propertyRepository.countByOwnerIdAndSubscriptionId(userId, subscription.getId());
        log.info("User {} already has {} active properties with subscription {}", 
            userId, activePropertiesCount, subscription.getId());
            
        // Calculate how many more properties can be activated
        int remainingSlots = activationLimit - (int) activePropertiesCount;
        if (remainingSlots <= 0) {
            log.info("No remaining property slots for user {} with subscription {}", 
                userId, subscription.getId());
            return List.of();
        }
        
        // Activate properties up to the limit
        List<Property> propertiesToActivate = inactiveProperties.stream()
            .limit(remainingSlots)
            .collect(Collectors.toList());
            
        log.info("Activating {} properties for user {} with subscription {}", 
            propertiesToActivate.size(), userId, subscription.getId());
            
        for (Property property : propertiesToActivate) {
            property.setActive(true);
            property.setSubscriptionId(subscription.getId());
            property.setSubscriptionExpiry(subscription.getEndDate());
            
            // Save the property
            Property savedProperty = propertyRepository.save(property);
            
            log.info("Activated property {} for user {} with subscription {}", 
                property.getId(), userId, subscription.getId());
                
            // Send property activation email notification
            try {
                propertyEmailService.sendPropertyActivatedNotification(savedProperty);
                log.info("Property activation notification email sent for property ID: {}", savedProperty.getId());
            } catch (Exception e) {
                log.error("Failed to send property activation notification email: {}", e.getMessage(), e);
            }
        }
        
        return propertiesToActivate;
    }
    
    /**
     * Deactivate properties associated with an expired subscription
     * 
     * @param subscription The expired subscription
     * @return List of deactivated properties
     */
    @Transactional
    private List<Property> deactivatePropertiesBySubscription(Subscription subscription) {
        // Find properties associated with this subscription
        List<Property> properties = propertyRepository.findBySubscriptionId(subscription.getId());
        log.info("Found {} properties associated with expired subscription {}", 
            properties.size(), subscription.getId());
            
        if (properties.isEmpty()) {
            return List.of();
        }
        
        // Deactivate all properties
        for (Property property : properties) {
            property.setActive(false);
            propertyRepository.save(property);
            
            log.info("Deactivated property {} due to subscription {} expiration", 
                property.getId(), subscription.getId());
                
            // Send property deactivation email notification
            try {
                propertyEmailService.sendPropertyDeactivatedNotification(property);
                log.info("Property deactivation notification email sent for property ID: {}", property.getId());
            } catch (Exception e) {
                log.error("Failed to send property deactivation notification email: {}", e.getMessage(), e);
            }
        }
        
        return properties;
    }
    
    @Override
    @Transactional
    public void confirmPayment(Long subscriptionId, String paymentReferenceId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + subscriptionId));
            
        // Update payment reference and confirm subscription
        subscription.setPaymentReference(paymentReferenceId);
        subscription.setPaymentConfirmed(true);
        subscriptionRepository.save(subscription);
        
        // Update district revenue status to PAID
        districtRevenueService.updateSubscriptionPaymentStatus(subscriptionId, PaymentStatus.PAID, paymentReferenceId);
        
        log.info("Payment confirmed for subscription {}: {}", subscriptionId, paymentReferenceId);
    }
    
    // Scheduled task to check and update long-pending transactions
    @Scheduled(cron = "0 0 * * * *") // Run every hour
    @Transactional
    public void checkPendingTransactions() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        int count = districtRevenueService.handleStuckPendingTransactions(cutoffTime);
        if (count > 0) {
            log.warn("Found and processed {} stuck pending transactions", count);
        }
    }
    @Override
    public boolean hasActiveProfileSubscription(Long userId) {

        if (userId == null) return false;

        return subscriptionRepository.existsActiveByUserIdAndPlanType(
                userId,
                PlanType.PROFILE,
                LocalDateTime.now()
        );
    }

}
