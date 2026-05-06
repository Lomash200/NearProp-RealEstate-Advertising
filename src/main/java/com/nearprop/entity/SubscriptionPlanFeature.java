package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_plan_features")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "plan_name", nullable = false, unique = true)
@Column(name = "plan_name", nullable = false)
    private String planName;
    
    @Column(name = "plan_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlanType planType;

    @Column(name = "max_properties", nullable = false)
    private Integer maxProperties;
    
    @Column(name = "max_reels_per_property", nullable = false)
    private Integer maxReelsPerProperty;
    
    @Column(name = "max_total_reels", nullable = false)
    private Integer maxTotalReels;
    
    @Column(name = "max_reel_duration_seconds", nullable = false)
    private Integer maxReelDurationSeconds;
    
    @Column(name = "max_reel_file_size_mb", nullable = false)
    private Integer maxReelFileSizeMb;
    
    @Column(name = "allowed_video_formats", nullable = false)
    private String allowedVideoFormats; // Comma separated values: mp4,mov,etc

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "monthly_price", nullable = false)
    private Double monthlyPrice;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum PlanType {
        USER, SELLER, ADVISOR, DEVELOPER, FRANCHISEE
    }

    public int getIntValue() {
        if (this.maxReelsPerProperty != null) {
            return this.maxReelsPerProperty;
        }
        return 5; // Default value
    }

    public void setValue(String value) {
        try {
            this.maxReelsPerProperty = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            this.maxReelsPerProperty = 5; // Default value
        }
    }
} 