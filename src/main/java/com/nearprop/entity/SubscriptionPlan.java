package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_plans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
//    @Column(nullable = false, unique = true)
@Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType type;

    @Column(name = "type_s", length = 255)
    private String type_s;

    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(name = "marketing_fee")
    private BigDecimal marketingFee;
    
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;
    
    // Plan-specific limitations
    @Column(name = "max_properties")
    private Integer maxProperties;
    
    @Column(name = "max_reels_per_property")
    private Integer maxReelsPerProperty;
    
    @Column(name = "max_total_reels")
    private Integer maxTotalReels;
    
    // Content retention periods (in days)
    @Column(name = "content_hide_after_days", nullable = false)
    private Integer contentHideAfterDays = 0; // After subscription expires
    
    @Column(name = "content_delete_after_days", nullable = false)
    private Integer contentDeleteAfterDays = 60; // After subscription expires
    
    @Column(nullable = false)
    private boolean active;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Plan types correspond to user roles
    public enum PlanType {
        SELLER,
        ADVISOR, 
        DEVELOPER,
        FRANCHISEE,
        PROPERTY,
        PROFILE
    }
    
    // Helper method to check if plan is a franchise plan
    @Transient
    public boolean isFranchisePlan() {
        return this.type == PlanType.FRANCHISEE;
    }
    
    // Helper method to determine if plan has unlimited properties
    @Transient
    public boolean hasUnlimitedProperties() {
        return this.maxProperties == null || this.maxProperties <= 0;
    }
    
    // Helper method to determine if plan has unlimited reels
    @Transient
    public boolean hasUnlimitedReels() {
        return this.maxReelsPerProperty == null || this.maxReelsPerProperty <= 0 || this.maxTotalReels == null || this.maxTotalReels <= 0;
    }
    
    public boolean shouldHideContent() {
        return this.contentHideAfterDays != null && this.contentHideAfterDays > 0;
    }
    
    public boolean shouldDeleteContent() {
        return this.contentDeleteAfterDays != null && this.contentDeleteAfterDays > 0;
    }
} 