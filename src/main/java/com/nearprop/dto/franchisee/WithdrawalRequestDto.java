package com.nearprop.dto.franchisee;

import com.nearprop.entity.FranchiseeWithdrawalRequest.WithdrawalStatus;
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
public class WithdrawalRequestDto {
    private Long id;
    private Long franchiseeDistrictId;
    private String districtName;
    private String state;
    private Long franchiseeId;
    private String franchiseeName;
    private Long bankDetailId;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private BigDecimal requestedAmount;
    private String reason;
    private WithdrawalStatus status;
    private Long processedById;
    private String processedByName;
    private LocalDateTime processedAt;
    private String adminComments;
    private String paymentReference;
    private LocalDateTime paymentDate;
    private String screenshotUrl;
    private BigDecimal availableBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for transaction details
    private String transactionType;
    private String transactionId;
    
    // Balance tracking fields
    private BigDecimal originalBalance;
    private BigDecimal updatedBalance;
} 