package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    //COUPON
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String code;
    
    @Column(unique = true, nullable = false)
    private String permanentId;
    
    @Column(length = 255)
    private String description;
    
    @Column(name = "discount_amount")
    private BigDecimal discountAmount;
    
    @Column(name = "discount_percentage")
    private Integer discountPercentage;
    
    @Column(name = "max_discount")
    private BigDecimal maxDiscount;
    
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;
    
    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;
    
    @Column(name = "max_uses")
    private Integer maxUses;
    
    @Column(name = "current_uses")
    private Integer currentUses;
    
    @Column(name = "is_active")
    private boolean active;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type")
    private SubscriptionPlan.PlanType subscriptionType;
    
    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT
    }
    
    /**
     * Calculate the discount amount for a given price
     * 
     * @param originalPrice The original price
     * @return The discount amount
     */
    public BigDecimal calculateDiscount(BigDecimal originalPrice) {
        if (originalPrice == null) {
            return BigDecimal.ZERO;
        }
        
        if (discountType == DiscountType.PERCENTAGE && discountPercentage != null) {
            BigDecimal percentageDecimal = BigDecimal.valueOf(discountPercentage).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            BigDecimal discount = originalPrice.multiply(percentageDecimal);
            
            // Apply max discount cap if set
            if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
                return maxDiscount;
            }
            return discount;
        } else if (discountType == DiscountType.FIXED_AMOUNT && discountAmount != null) {
            return discountAmount;
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Check if the coupon is valid for the given price and current time
     * 
     * @param price The price to check
     * @param currentTime The current time
     * @return True if the coupon is valid, false otherwise
     */
    public boolean isValid(BigDecimal price, LocalDateTime currentTime) {
        if (!active) {
            return false;
        }
        
        if (currentTime.isBefore(validFrom) || currentTime.isAfter(validUntil)) {
            return false;
        }
        
        if (maxUses != null && currentUses >= maxUses) {
            return false;
        }
        
        return true;
    }
} 