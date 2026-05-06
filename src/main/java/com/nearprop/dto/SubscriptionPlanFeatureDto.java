package com.nearprop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nearprop.entity.SubscriptionPlanFeature;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionPlanFeatureDto {
    
    private Long id;
    
    @NotBlank(message = "Plan name is required")
    private String planName;
    
    @NotNull(message = "Plan type is required")
    private SubscriptionPlanFeature.PlanType planType;
    
    @Min(value = 0, message = "Max properties must be at least 0")
    private Integer maxProperties;
    
    @Min(value = 0, message = "Max reels per property must be at least 0")
    private Integer maxReelsPerProperty;
    
    @Min(value = 0, message = "Max total reels must be at least 0")
    private Integer maxTotalReels;
    
    @Min(value = 0, message = "Max reel duration must be at least 0")
    private Integer maxReelDurationSeconds;
    
    @Min(value = 0, message = "Max reel file size must be at least 0")
    private Integer maxReelFileSizeMb;
    
    private String allowedVideoFormats;
    
    private Boolean isActive;
    
    @Min(value = 0, message = "Monthly price must be at least 0")
    private Double monthlyPrice;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 