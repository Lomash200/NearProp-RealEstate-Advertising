package com.nearprop.dto;

import com.nearprop.entity.Coupon;
import com.nearprop.entity.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequestDto {
    
    @NotBlank(message = "Coupon code is required")
    @Size(min = 3, max = 20, message = "Coupon code must be between 3 and 20 characters")
    private String code;
    
    private String description;
    
    private BigDecimal discountAmount;
    
    @Min(value = 1, message = "Discount percentage must be at least 1")
    @Max(value = 100, message = "Discount percentage cannot exceed 100")
    private Integer discountPercentage;
    
    private BigDecimal maxDiscount;
    
    @NotNull(message = "Valid from date is required")
    private LocalDateTime validFrom;
    
    @NotNull(message = "Valid until date is required")
    private LocalDateTime validUntil;
    
    private Integer maxUses;
    
    private boolean active;
    
    @NotNull(message = "Discount type is required")
    private Coupon.DiscountType discountType;
    
    private SubscriptionPlan.PlanType subscriptionType;
} 