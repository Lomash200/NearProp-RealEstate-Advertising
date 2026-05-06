package com.nearprop.dto.franchisee;

import com.nearprop.entity.FranchiseeWithdrawalRequest.WithdrawalStatus;
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
public class WithdrawalRequestResponseDto {
    
    @NotNull(message = "Status is required")
    private WithdrawalStatus status;
    
    @Size(max = 1000, message = "Comments must not exceed 1000 characters")
    private String adminComments;
    
    private String screenshotUrl;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
    private String mobileNumber;
    private String paymentReference;
    
    // Additional fields for transaction details
    private String transactionType;
    private String transactionId;
    
    // Balance tracking fields
    private BigDecimal originalBalance;
    private BigDecimal updatedBalance;
} 