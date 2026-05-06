package com.nearprop.dto.franchisee;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueReportDto {
    private Long id;
    
    // Report metadata
    private Integer year;
    private Integer month;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime generatedAt;
    
    private String reportStatus;
    
    // Franchisee details
    private Long franchiseeId;
    private String franchiseeName;
    private String businessName;
    
    // District details
    private Long franchiseeDistrictId;
    private Long districtId;
    private String districtName;
    private String state;
    
    // Revenue details
    private BigDecimal totalRevenue;
    private BigDecimal franchiseeCommission;
    private BigDecimal adminShare;
    private Integer totalSubscriptions;
    private Integer newSubscriptions;
    private Integer renewedSubscriptions;
    
    // Payment details
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDueDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;
    
    private String paymentReference;
    private String paymentMethod;
    private String adminComments;
    
    // Bank details
    private Long bankDetailId;
    private String accountName;
    private String accountNumber;
    private String bankName;
    private String ifscCode;
    
    // Withdrawal details
    private BigDecimal emergencyWithdrawalsAmount;
    private Integer emergencyWithdrawalsCount;
    
    // Balance calculation
    private BigDecimal previousBalance;
    private BigDecimal currentBalance;
    private BigDecimal finalPayableAmount;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private Long processedById;
    private String processedByName;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime processedAt;
} 