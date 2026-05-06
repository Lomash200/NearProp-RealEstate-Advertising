package com.nearprop.dto.admin;

import com.nearprop.entity.SubscriptionPlan.PlanType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionPlanDto {
    
    @NotBlank(message = "Plan name is required")
    private String name;
    
    @NotBlank(message = "Plan description is required")
    private String description;
    
    @NotNull(message = "Plan type is required")
    private PlanType type;

    private String type_s;
    
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private BigDecimal price;
    
    @Min(value = 0, message = "Marketing fee cannot be negative")
    private BigDecimal marketingFee;
    
    @NotNull(message = "Duration is required")
    @Min(value = 0, message = "Duration must be at least 1 day")
    private Integer durationDays;
    
    @Min(value = -1, message = "Maximum properties must be at least -1 (-1 for unlimited)")
    private Integer maxProperties;
    
    @Min(value = -1, message = "Maximum reels per property must be at least -1 (-1 for unlimited)")
    private Integer maxReelsPerProperty;
    
    @Min(value = -1, message = "Maximum total reels must be at least -1 (-1 for unlimited)")
    private Integer maxTotalReels;
    
    @Min(value = 0, message = "Content hide period must be at least 0 days")
    @Builder.Default
    private Integer contentHideAfterDays = 0;
    
    @Min(value = 0, message = "Content delete period must be at least 1 day")
    @Builder.Default
    private Integer contentDeleteAfterDays = 60;
    
    @Builder.Default
    private Boolean active = true;
} 