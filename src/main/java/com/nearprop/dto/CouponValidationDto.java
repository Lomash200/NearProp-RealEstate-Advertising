package com.nearprop.dto;

import com.nearprop.entity.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidationDto {
    
    @NotBlank(message = "Coupon code is required")
    private String code;
    
    private BigDecimal orderAmount;
    
    private Long planId;
} 