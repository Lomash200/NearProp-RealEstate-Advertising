package com.nearprop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidationResponseDto {
    
    private String code;
    private String permanentId;
    private String description;
    private boolean valid;
    private BigDecimal discountAmount;
    private BigDecimal originalPrice;
    private BigDecimal finalPrice;
    private String message;
    private LocalDateTime validUntil;
    
    // Plan information
    private Long planId;
    private String planName;
    private String planType;
} 