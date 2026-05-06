package com.nearprop.dto.franchisee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalHistoryDto {
    private Long franchiseeId;
    private String franchiseeName;
    private Long franchiseeDistrictId;
    private String districtName;
    private String state;
    private BigDecimal totalCommission;
    private BigDecimal availableBalance;
    private List<WithdrawalHistoryEntryDto> history;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WithdrawalHistoryEntryDto {
        private Long requestId;
        private BigDecimal amount;
        private String status;
        private LocalDateTime processedAt;
        private Long processedById;
        private String processedByName;
        private BigDecimal originalBalance;
        private BigDecimal updatedBalance;
        private String transactionType;
        private String transactionId;
        private String paymentReference;
    }
} 