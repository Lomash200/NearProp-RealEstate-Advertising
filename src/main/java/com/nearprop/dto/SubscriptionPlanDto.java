package com.nearprop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nearprop.entity.SubscriptionPlan.PlanType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionPlanDto {
    
    private Long id;
    
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
    
    private BigDecimal marketingFee;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;
    
    private Integer maxProperties;
    
    private Integer maxReelsPerProperty;
    
    private Integer maxTotalReels;
    
    private Integer contentHideAfterDays;
    
    private Integer contentDeleteAfterDays;
    
    @NotNull(message = "Active status is required")
    private Boolean active;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private Boolean hasUnlimitedProperties;
    
    private Boolean hasUnlimitedReels;
} 