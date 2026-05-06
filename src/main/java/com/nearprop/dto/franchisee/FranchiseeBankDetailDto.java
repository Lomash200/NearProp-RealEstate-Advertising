package com.nearprop.dto.franchisee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseeBankDetailDto {
    private Long id;
    
    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Account name must be at most 100 characters")
    private String accountName;
    
    @NotBlank(message = "Account number is required")
    @Size(max = 30, message = "Account number must be at most 30 characters")
    private String accountNumber;
    
    @NotBlank(message = "IFSC code is required")
    @Size(max = 20, message = "IFSC code must be at most 20 characters")
    private String ifscCode;
    
    @NotBlank(message = "Bank name is required")
    @Size(max = 100, message = "Bank name must be at most 100 characters")
    private String bankName;
    
    @Size(max = 100, message = "Branch name must be at most 100 characters")
    private String branchName;
    
    @Size(max = 30, message = "Account type must be at most 30 characters")
    private String accountType;
    
    @Size(max = 50, message = "UPI ID must be at most 50 characters")
    private String upiId;
    
    private boolean isPrimary;
    
    private boolean isVerified;
}
