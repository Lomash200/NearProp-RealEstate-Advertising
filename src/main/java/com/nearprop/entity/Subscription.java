package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(name = "marketing_fee")
    private BigDecimal marketingFee;
    
    @Column(name = "is_renewal")
    private boolean isRenewal;
    
    @Column(name = "previous_subscription_id")
    private Long previousSubscriptionId;
    
    @Column(name = "district_id")
    private Long districtId;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;
    
    @Column(name = "payment_reference")
    private String paymentReference;
    
    @Column(name = "payment_confirmed")
    private Boolean paymentConfirmed = false;
    
    @Column(name = "auto_renew")
    private boolean autoRenew;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "content_hidden_at")
    private LocalDateTime contentHiddenAt;
    
    @Column(name = "content_deleted_at")
    private LocalDateTime contentDeletedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
    
    @Column(name = "coupon_code")
    private String couponCode;
    
    @Column(name = "original_price")
    private BigDecimal originalPrice;
    
    @Column(name = "discount_amount")
    private BigDecimal discountAmount;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum SubscriptionStatus {
        PENDING_PAYMENT, ACTIVE, EXPIRED, CANCELLED, CONTENT_HIDDEN, CONTENT_DELETED
    }
    
    // Helper method to check if subscription is active
    @Transient
    public boolean isActive() {
        return this.status == SubscriptionStatus.ACTIVE || this.status == SubscriptionStatus.PENDING_PAYMENT;
    }
    
    // Helper method to check if subscription is in grace period
    @Transient
    public boolean isInGracePeriod() {
        if (this.status != SubscriptionStatus.EXPIRED) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime gracePeriodEnd = this.endDate.plusDays(3); // 3-day grace period
        return now.isBefore(gracePeriodEnd);
    }
    
    // Helper method to check if content should be hidden
    @Transient
    public boolean shouldHideContent() {
        if (this.status != SubscriptionStatus.EXPIRED || this.contentHiddenAt != null) {
            return false;
        }
        
        // Skip content hiding for franchise plans
        if (this.plan.isFranchisePlan()) {
            return false;
        }
        
        // Check if content hide period has passed
        if (!this.plan.shouldHideContent()) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime hideDate = this.endDate.plusDays(this.plan.getContentHideAfterDays());
        return now.isAfter(hideDate);
    }
    
    // Helper method to check if content should be deleted
    @Transient
    public boolean shouldDeleteContent() {
        if (this.status != SubscriptionStatus.CONTENT_HIDDEN || this.contentDeletedAt != null) {
            return false;
        }
        
        // Skip content deletion for franchise plans
        if (this.plan.isFranchisePlan()) {
            return false;
        }
        
        // Check if content delete period has passed
        if (!this.plan.shouldDeleteContent()) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deleteDate = this.endDate.plusDays(this.plan.getContentDeleteAfterDays());
        return now.isAfter(deleteDate);
    }
    
    // Helper method to get total amount
    @Transient
    public BigDecimal getTotalAmount() {
        BigDecimal total = this.price;
        if (this.marketingFee != null) {
            total = total.add(this.marketingFee);
        }
        return total;
    }
}