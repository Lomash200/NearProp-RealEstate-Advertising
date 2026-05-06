package com.nearprop.controller.admin;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.CouponDto;
import com.nearprop.dto.CouponRequestDto;
import com.nearprop.entity.SubscriptionPlan;
import com.nearprop.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
@Slf4j
public class AdminCouponController {
    
    private final CouponService couponService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponDto>> createCoupon(@Valid @RequestBody CouponRequestDto requestDto) {
        log.info("REST request to create coupon: {}", requestDto.getCode());
        CouponDto result = couponService.createCoupon(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Success", result));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponDto>> updateCoupon(
            @PathVariable Long id,
            @Valid @RequestBody CouponRequestDto requestDto) {
        log.info("REST request to update coupon: {}", id);
        CouponDto result = couponService.updateCoupon(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponDto>> getCouponById(@PathVariable Long id) {
        log.info("REST request to get coupon: {}", id);
        CouponDto result = couponService.getCouponById(id);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CouponDto>>> getAllCoupons(Pageable pageable) {
        log.info("REST request to get all coupons");
        Page<CouponDto> result = couponService.getAllCoupons(pageable);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<Page<CouponDto>>> getActiveCoupons(Pageable pageable) {
        log.info("REST request to get active coupons");
        Page<CouponDto> result = couponService.getActiveCoupons(pageable);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id) {
        log.info("REST request to delete coupon: {}", id);
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponDto>> activateCoupon(@PathVariable Long id) {
        log.info("REST request to activate coupon: {}", id);
        CouponDto result = couponService.activateCoupon(id);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }
    
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponDto>> deactivateCoupon(@PathVariable Long id) {
        log.info("REST request to deactivate coupon: {}", id);
        CouponDto result = couponService.deactivateCoupon(id);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }
    
    @GetMapping("/plan-type/{planType}")
    public ResponseEntity<ApiResponse<List<CouponDto>>> getValidCouponsByPlanType(
            @PathVariable SubscriptionPlan.PlanType planType) {
        log.info("REST request to get valid coupons for plan type: {}", planType);
        List<CouponDto> result = couponService.getValidCouponsByPlanType(planType);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }
} 