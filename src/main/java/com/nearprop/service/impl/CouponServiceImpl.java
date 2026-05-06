package com.nearprop.service.impl;

import com.nearprop.dto.CouponDto;
import com.nearprop.dto.CouponRequestDto;
import com.nearprop.dto.CouponValidationDto;
import com.nearprop.dto.CouponValidationResponseDto;
import com.nearprop.entity.Coupon;
import com.nearprop.entity.SubscriptionPlan;
import com.nearprop.entity.User;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.mapper.CouponMapper;
import com.nearprop.repository.CouponRepository;
import com.nearprop.repository.SubscriptionPlanRepository;
import com.nearprop.service.CouponService;
import com.nearprop.service.CouponUsageService;
import com.nearprop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImpl implements CouponService {
    
    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;
    private final UserService userService;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final CouponUsageService couponUsageService;


    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "coupons", allEntries = true)
    public CouponDto createCoupon(CouponRequestDto requestDto) {
        log.info("Creating new coupon with code: {}", requestDto.getCode());
        
        User admin = userService.getCurrentUser();
        Coupon coupon = couponMapper.toEntity(requestDto, admin);
        
        Coupon savedCoupon = couponRepository.save(coupon);
        log.info("Coupon created successfully with ID: {} and permanent ID: {}", savedCoupon.getId(), savedCoupon.getPermanentId());
        
        return couponMapper.toDto(savedCoupon);
    }
    @Override
    @Transactional
    public void updateStatus(Long couponId, Boolean active) {
        if (Boolean.TRUE.equals(active)) {
            activateCoupon(couponId);
        } else {
            deactivateCoupon(couponId);
        }
    }


    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "coupons", allEntries = true)
    public CouponDto updateCoupon(Long id, CouponRequestDto requestDto) {
        log.info("Updating coupon with ID: {}", id);
        
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with ID: " + id));
        
        Coupon updatedCoupon = couponMapper.updateEntity(coupon, requestDto);
        Coupon savedCoupon = couponRepository.save(updatedCoupon);
        
        log.info("Coupon updated successfully with ID: {}", savedCoupon.getId());
        return couponMapper.toDto(savedCoupon);
    }
    
    @Override
    public CouponDto getCouponById(Long id) {
        log.info("Getting coupon by ID: {}", id);
        
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with ID: " + id));
        
        return couponMapper.toDto(coupon);
    }
    
    @Override
    public CouponDto getCouponByCode(String code) {
        log.info("Getting coupon by code: {}", code);
        
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with code: " + code));
        
        return couponMapper.toDto(coupon);
    }
    
    @Override
    public Page<CouponDto> getAllCoupons(Pageable pageable) {
        log.info("Getting all coupons with pagination");
        
        Page<Coupon> coupons = couponRepository.findAll(pageable);
        return coupons.map(couponMapper::toDto);
    }
    
    @Override
    @Cacheable(value = "coupons", key = "'active_' + #pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort")
    public Page<CouponDto> getActiveCoupons(Pageable pageable) {
        log.info("Getting active coupons with pagination");
        
        Page<Coupon> coupons = couponRepository.findByActive(true, pageable);
        return coupons.map(couponMapper::toDto);
    }
    
    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "coupons", allEntries = true)
    public void deleteCoupon(Long id) {
        log.info("Deleting coupon with ID: {}", id);
        
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with ID: " + id));
        
        couponRepository.delete(coupon);
        log.info("Coupon deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "coupons", allEntries = true)
    public CouponDto activateCoupon(Long id) {
        log.info("Activating coupon with ID: {}", id);
        
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with ID: " + id));
        
        coupon.setActive(true);
        Coupon savedCoupon = couponRepository.save(coupon);
        
        log.info("Coupon activated successfully with ID: {}", id);
        return couponMapper.toDto(savedCoupon);
    }
    
    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "coupons", allEntries = true)
    public CouponDto deactivateCoupon(Long id) {
        log.info("Deactivating coupon with ID: {}", id);
        
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with ID: " + id));
        
        coupon.setActive(false);
        Coupon savedCoupon = couponRepository.save(coupon);
        
        log.info("Coupon deactivated successfully with ID: {}", id);
        return couponMapper.toDto(savedCoupon);
    }
    
//    @Override
//    public CouponValidationResponseDto validateCoupon(CouponValidationDto validationDto) {
//        log.info("Validating coupon with code: {}", validationDto.getCode());
//
//        final LocalDateTime now = LocalDateTime.now();
//        final String code = validationDto.getCode().toUpperCase();
//        BigDecimal originalOrderAmount = validationDto.getOrderAmount();
//
//        // Get plan type from plan ID if provided
//        SubscriptionPlan.PlanType planType = null;
//        BigDecimal orderAmount = originalOrderAmount;
//        SubscriptionPlan plan = null;
//
//        if (validationDto.getPlanId() != null) {
//            plan = subscriptionPlanRepository.findById(validationDto.getPlanId())
//                    .orElse(null);
//            if (plan != null) {
//                planType = plan.getType();
//                // Always use plan price when a valid plan ID is provided
//                orderAmount = plan.getPrice();
//                originalOrderAmount = orderAmount; // Update original amount to match plan price
//                log.info("Using plan price: {} for plan ID: {}", orderAmount, validationDto.getPlanId());
//            } else {
//                // Return error response for invalid plan ID
//                log.warn("Invalid subscription plan ID: {}", validationDto.getPlanId());
//                return CouponValidationResponseDto.builder()
//                        .code(code)
//                        .valid(false)
//                        .originalPrice(originalOrderAmount)
//                        .finalPrice(originalOrderAmount)
//                        .message("Invalid subscription plan ID: " + validationDto.getPlanId())
//                        .build();
//            }
//        }
//
//        final SubscriptionPlan.PlanType finalPlanType = planType;
//        final BigDecimal finalOrderAmount = orderAmount;
//        final SubscriptionPlan finalPlan = plan;
//
//        log.info("Searching for valid coupon with code: {} at time: {}", code, now);
//
//        return couponRepository.findValidCouponByCode(code, now)
//                .map(coupon -> {
//                    log.info("Found coupon: {}, active: {}, validFrom: {}, validUntil: {}, maxUses: {}, currentUses: {}, discountType: {}, discountAmount: {}, discountPercentage: {}",
//                            coupon.getCode(), coupon.isActive(), coupon.getValidFrom(), coupon.getValidUntil(),
//                            coupon.getMaxUses(), coupon.getCurrentUses(), coupon.getDiscountType(),
//                            coupon.getDiscountAmount(), coupon.getDiscountPercentage());
//
//                    boolean isValid = coupon.isValid(finalOrderAmount, now);
//                    String message = isValid ? "Coupon is valid" : "Coupon is not valid";
//                    log.info("Coupon validity check: {}", isValid);
//
//                    if (isValid && finalPlanType != null) {
//                        if (coupon.getSubscriptionType() != null &&
//                            coupon.getSubscriptionType() != finalPlanType) {
//                            isValid = false;
//                            message = "Coupon is not valid for this subscription type";
//                            log.info("Coupon not valid for subscription type: {} (required: {})",
//                                    coupon.getSubscriptionType(), finalPlanType);
//                        }
//                    }
//
//                    final boolean finalIsValid = isValid;
//                    BigDecimal discountAmount = BigDecimal.ZERO;
//                    BigDecimal finalPrice = finalOrderAmount;
//
//                    if (finalIsValid) {
//                        discountAmount = coupon.calculateDiscount(finalOrderAmount);
//                        finalPrice = finalOrderAmount.subtract(discountAmount);
//                        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
//                            finalPrice = BigDecimal.ZERO;
//                        }
//                        log.info("Applied discount: {}, original price: {}, final price: {}",
//                                discountAmount, finalOrderAmount, finalPrice);
//                    }
//
//                    CouponValidationResponseDto.CouponValidationResponseDtoBuilder builder = CouponValidationResponseDto.builder()
//                            .code(coupon.getCode())
//                            .permanentId(coupon.getPermanentId())
//                            .description(coupon.getDescription())
//                            .valid(finalIsValid)
//                            .discountAmount(discountAmount)
//                            .originalPrice(finalOrderAmount)
//                            .finalPrice(finalPrice)
//                            .message(message)
//                            .validUntil(coupon.getValidUntil());
//
//                    // Add plan information if available
//                    if (finalPlan != null) {
//                        builder.planId(finalPlan.getId())
//                               .planName(finalPlan.getName())
//                               .planType(finalPlan.getType().name());
//                    }
//
//                    return builder.build();
//                })
//                .orElseGet(() -> {
//                    log.warn("Coupon not found or expired: {}", code);
//                    return CouponValidationResponseDto.builder()
//                            .code(code)
//                            .valid(false)
//                            .originalPrice(finalOrderAmount)
//                            .finalPrice(finalOrderAmount)
//                            .message("Coupon not found or expired")
//                            .build();
//                });
//    }
//


    @Override
    public CouponValidationResponseDto validateCoupon(CouponValidationDto validationDto) {
        log.info("Validating coupon with code: {}", validationDto.getCode());

        final LocalDateTime now = LocalDateTime.now();
        final String code = validationDto.getCode().toUpperCase();
        BigDecimal originalOrderAmount = validationDto.getOrderAmount();

        SubscriptionPlan.PlanType planType = null;
        BigDecimal orderAmount = originalOrderAmount;
        SubscriptionPlan plan = null;

        if (validationDto.getPlanId() != null) {
            plan = subscriptionPlanRepository.findById(validationDto.getPlanId()).orElse(null);
            if (plan == null) {
                return CouponValidationResponseDto.builder()
                        .code(code)
                        .valid(false)
                        .originalPrice(originalOrderAmount)
                        .finalPrice(originalOrderAmount)
                        .message("Invalid subscription plan ID")
                        .build();
            }
            planType = plan.getType();
            orderAmount = plan.getPrice();
            originalOrderAmount = orderAmount;
        }

        BigDecimal finalOrderAmount = orderAmount;
        SubscriptionPlan.PlanType finalPlanType = planType;
        SubscriptionPlan finalPlan = plan;

        return couponRepository.findValidCouponByCode(code, now)
                .map(coupon -> {

                    // 🔒 USER CAN USE COUPON ONLY ONCE (SAFE CHECK)
//                    try {
//                        couponUsageService.checkNotUsed(coupon.getId());
//                    } catch (Exception e) {
//                        return CouponValidationResponseDto.builder()
//                                .code(coupon.getCode())
//                                .valid(false)
//                                .originalPrice(finalOrderAmount)
//                                .finalPrice(finalOrderAmount)
//                                .message(e.getMessage())
//                                .build();
//                    }


                    boolean isValid = coupon.isValid(finalOrderAmount, now);
                    String message = isValid ? "Coupon is valid" : "Coupon is not valid";

                    if (isValid && finalPlanType != null &&
                            coupon.getSubscriptionType() != null &&
                            coupon.getSubscriptionType() != finalPlanType) {
                        isValid = false;
                        message = "Coupon is not valid for this subscription type";
                    }

                    BigDecimal discountAmount = BigDecimal.ZERO;
                    BigDecimal finalPrice = finalOrderAmount;

                    if (isValid) {
                        discountAmount = coupon.calculateDiscount(finalOrderAmount);
                        finalPrice = finalOrderAmount.subtract(discountAmount);
                        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
                            finalPrice = BigDecimal.ZERO;
                        }
                    }

                    CouponValidationResponseDto.CouponValidationResponseDtoBuilder builder =
                            CouponValidationResponseDto.builder()
                                    .code(coupon.getCode())
                                    .permanentId(coupon.getPermanentId())
                                    .description(coupon.getDescription())
                                    .valid(isValid)
                                    .discountAmount(discountAmount)
                                    .originalPrice(finalOrderAmount)
                                    .finalPrice(finalPrice)
                                    .message(message)
                                    .validUntil(coupon.getValidUntil());

                    if (finalPlan != null) {
                        builder.planId(finalPlan.getId())
                                .planName(finalPlan.getName())
                                .planType(finalPlan.getType().name());
                    }

                    return builder.build();
                })
                .orElseGet(() -> CouponValidationResponseDto.builder()
                        .code(code)
                        .valid(false)
                        .originalPrice(finalOrderAmount)
                        .finalPrice(finalOrderAmount)
                        .message("Coupon not found or expired")
                        .build());
    }


//    @Override
//    public CouponValidationResponseDto applyCoupon(String code, BigDecimal originalPrice) {
//        log.info("Applying coupon with code: {} for price: {}", code, originalPrice);
//
//        CouponValidationDto validationDto = new CouponValidationDto();
//        validationDto.setCode(code);
//        validationDto.setOrderAmount(originalPrice);
//
//        CouponValidationResponseDto responseDto = validateCoupon(validationDto);
//
//        if (responseDto.isValid()) {
//            // Increment usage count if coupon is valid and applied
//            final String upperCaseCode = code.toUpperCase();
//            couponRepository.findByCode(upperCaseCode).ifPresent(coupon -> {
//                coupon.setCurrentUses(coupon.getCurrentUses() + 1);
//                couponRepository.save(coupon);
//                log.info("Incremented usage count for coupon: {}", upperCaseCode);
//            });
//        }
//
//        return responseDto;
//    }

    @Override
    @Transactional
    public CouponValidationResponseDto applyCoupon(String code, BigDecimal originalPrice) {
        log.info("Applying coupon with code: {} for price: {}", code, originalPrice);

        // 🔹 Fetch coupon FIRST
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

        // 🔒 HARD CHECK – APPLY LEVEL (MOST IMPORTANT)
        couponUsageService.checkNotUsed(coupon.getId());

        // 🔹 Validate coupon (price, expiry, subscription etc.)
        CouponValidationDto validationDto = CouponValidationDto.builder()
                .code(code)
                .orderAmount(originalPrice)
                .build();

        CouponValidationResponseDto responseDto = validateCoupon(validationDto);

        if (!responseDto.isValid()) {
            return responseDto;
        }

        // ✅ MARK AS USED (ONLY ONCE)
        User user = userService.getCurrentUser();
        couponUsageService.markUsed(user, coupon);

        // 🔢 GLOBAL USAGE COUNT
        coupon.setCurrentUses(
                coupon.getCurrentUses() == null ? 1 : coupon.getCurrentUses() + 1
        );
        couponRepository.save(coupon);

        log.info("Coupon applied successfully. Coupon: {}, User: {}", coupon.getCode(), user.getId());

        return responseDto;
    }


    @Override
    public List<CouponDto> getValidCouponsByPlanType(SubscriptionPlan.PlanType planType) {
        log.info("Getting valid coupons for plan type: {}", planType);
        
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> coupons = couponRepository.findValidCouponsByPlanType(now, planType);
        
        return coupons.stream()
                .map(couponMapper::toDto)
                .collect(Collectors.toList());
    }
} 