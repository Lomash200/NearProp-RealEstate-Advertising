package com.nearprop.dto.franchisee;

import com.nearprop.dto.UserDto;
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
public class FranchiseeProfileDto {
    private Long id;
    private UserDto userDetails;
    
    // Basic business info
    private String businessName;
    private String businessAddress;
    private String businessRegistrationNumber;
    private String gstNumber;
    private String panNumber;
    private String aadharNumber;
    
    // Contact info
    private String contactEmail;
    private String contactPhone;
    private Integer yearsOfExperience;
    
    // Bank details
    private FranchiseeBankDetailDto bankDetails;
    
    // District information
    private List<FranchiseeDistrictDto> assignedDistricts;
    
    // Revenue information
    private BigDecimal totalRevenue;
    private BigDecimal totalCommission;
    private BigDecimal availableBalance;
    private BigDecimal withdrawnAmount;
    private Integer totalProperties;
    private Integer totalTransactions;
    private Integer completedTransactions;
    
    // Revenue statistics by district
    private List<FranchiseeDistrictRevenueDto> districtRevenues;
    
    // Verification status
    private boolean isVerified;
    private LocalDateTime joinDate;
    
    // Document information
    private List<String> documentUrls;
}
