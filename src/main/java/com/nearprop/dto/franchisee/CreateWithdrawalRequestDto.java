package com.nearprop.dto.franchisee;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWithdrawalRequestDto {
    
    @NotNull(message = "Franchisee district ID is required")
    private Long franchiseeDistrictId;
    
    @NotNull(message = "Requested amount is required")
    @Min(value = 1, message = "Requested amount must be at least 1")
    private BigDecimal requestedAmount;
    
    @NotBlank(message = "Reason is required")
    @Size(min = 10, max = 1000, message = "Reason must be between 10 and 1000 characters")
    private String reason;
    
    // Optional - if not provided, will use the primary bank account
    private Long bankDetailId;
} 