package com.nearprop.controller;

import com.nearprop.dto.*;
import com.nearprop.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
@Slf4j
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<CouponValidationResponseDto>> validateCoupon(
            @RequestBody CouponValidationDto validationDto) {
        log.info("Validating coupon: {}", validationDto.getCode());
        CouponValidationResponseDto result = couponService.validateCoupon(validationDto);

        // Check if validation failed and return error response
        if (!result.isValid()) {
            return ResponseEntity.ok(ApiResponse.error(result.getMessage()));
        }

        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }

//    @GetMapping("/apply")
//    public ResponseEntity<ApiResponse<CouponApplyResponseDto>> applyCoupon(
//            @RequestParam String code,
//            @RequestParam BigDecimal amount,
//            @RequestParam(required = false) Long planId) {
//
//        log.info("Applying coupon: {} for amount: {} and planId: {}", code, amount, planId);
//
//        CouponValidationDto validationDto = CouponValidationDto.builder()
//                .code(code)
//                .orderAmount(amount)
//                .planId(planId)
//                .build();
//
//        CouponValidationResponseDto validationResult = couponService.validateCoupon(validationDto);
//
//        if (!validationResult.isValid()) {
//            return ResponseEntity.ok(ApiResponse.error(validationResult.getMessage()));
//        }
//
//        CouponApplyResponseDto result = CouponApplyResponseDto.builder()
//                .couponCode(code)
//                .originalAmount(amount)
//                .discountAmount(validationResult.getDiscountAmount())
//                .finalAmount(validationResult.getFinalPrice())
//                .message("Coupon applied successfully")
//                .build();
//
//        return ResponseEntity.ok(ApiResponse.success("Success", result));
//    }

    @GetMapping("/apply")
    public ResponseEntity<ApiResponse<CouponApplyResponseDto>> applyCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) Long planId) {

        // 🔥 IMPORTANT: APPLY SERVICE METHOD CALL
        CouponValidationResponseDto response =
                couponService.applyCoupon(code, amount);

        if (!response.isValid()) {
            return ResponseEntity.ok(ApiResponse.error(response.getMessage()));
        }

        CouponApplyResponseDto result = CouponApplyResponseDto.builder()
                .couponCode(code)
                .originalAmount(amount)
                .discountAmount(response.getDiscountAmount())
                .finalAmount(response.getFinalPrice())
                .message("Coupon applied successfully")
                .build();

        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<CouponDto>> activateCoupon(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Coupon activated successfully",
                        couponService.activateCoupon(id)
                )
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<CouponDto>> deactivateCoupon(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Coupon deactivated successfully",
                        couponService.deactivateCoupon(id)
                )
        );
    }
}