package com.nearprop.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nearprop.entity.FranchiseeDistrict;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActiveFranchiseeDTO {
    private Long id;
    private boolean active;
    private BigDecimal availableBalance;
    private String contactEmail;
    private String contactPhone;
    private LocalDateTime createdAt;
    private Integer totalProperties;
    private String districtName;
    private LocalDateTime endDate;
    private String officeAddress;
    private BigDecimal revenueSharePercentage;
    private LocalDateTime startDate;
    private String state;
    private FranchiseeDistrict.FranchiseeStatus status;
    private BigDecimal totalRevenue;
    private Integer totalTransactions;
    private BigDecimal totalCommission;
    private Integer totalWithdrawals; // count of withdrawals
    private LocalDateTime updatedAt;
    private String withdrawalHistory; // JSON string
    private Long userId;
    private Long districtId;
}