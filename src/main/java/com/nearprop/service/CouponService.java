package com.nearprop.service;

import com.nearprop.dto.CouponDto;
import com.nearprop.dto.CouponRequestDto;
import com.nearprop.dto.CouponValidationDto;
import com.nearprop.dto.CouponValidationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CouponService {
    
    /**
     * Create a new coupon
     * 
     * @param requestDto The coupon request DTO
     * @return The created coupon DTO
     */
    CouponDto createCoupon(CouponRequestDto requestDto);
    
    /**
     * Update an existing coupon
     * 
     * @param id The coupon ID
     * @param requestDto The coupon request DTO
     * @return The updated coupon DTO
     */
    CouponDto updateCoupon(Long id, CouponRequestDto requestDto);
    
    /**
     * Get a coupon by ID
     * 
     * @param id The coupon ID
     * @return The coupon DTO
     */
    CouponDto getCouponById(Long id);
    
    /**
     * Get a coupon by code
     * 
     * @param code The coupon code
     * @return The coupon DTO
     */
    CouponDto getCouponByCode(String code);
    
    /**
     * Get all coupons with pagination
     * 
     * @param pageable The pagination information
     * @return Page of coupon DTOs
     */
    Page<CouponDto> getAllCoupons(Pageable pageable);
    
    /**
     * Get all active coupons with pagination
     * 
     * @param pageable The pagination information
     * @return Page of coupon DTOs
     */
    Page<CouponDto> getActiveCoupons(Pageable pageable);
    
    /**
     * Delete a coupon by ID
     * 
     * @param id The coupon ID
     */
    void deleteCoupon(Long id);
    
    /**
     * Activate a coupon
     * 
     * @param id The coupon ID
     * @return The updated coupon DTO
     */
    CouponDto activateCoupon(Long id);
    
    /**
     * Deactivate a coupon
     * 
     * @param id The coupon ID
     * @return The updated coupon DTO
     */
    CouponDto deactivateCoupon(Long id);
    
    /**
     * Validate a coupon
     * 
     * @param validationDto The coupon validation DTO
     * @return The coupon validation response DTO
     */
    CouponValidationResponseDto validateCoupon(CouponValidationDto validationDto);
    
    /**
     * Apply a coupon to get the discounted price
     * 
     * @param code The coupon code
     * @param originalPrice The original price
     * @return The coupon validation response DTO
     */
    CouponValidationResponseDto applyCoupon(String code, java.math.BigDecimal originalPrice);
    
    /**
     * Get all valid coupons for a subscription plan type
     * 
     * @param planType The subscription plan type
     * @return List of coupon DTOs
     */
    List<CouponDto> getValidCouponsByPlanType(com.nearprop.entity.SubscriptionPlan.PlanType planType);

    void updateStatus(Long couponId, Boolean active);

} 