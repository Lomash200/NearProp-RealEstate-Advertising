package com.nearprop.dto.franchisee;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nearprop.dto.UserSummaryDto;
import com.nearprop.entity.FranchiseRequest.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FranchiseRequestDto {
    
    private Long id;
    
    private UserSummaryDto user;
    
    private Long districtId;
    
    private String districtName;
    
    private String state;
    
    private String businessName;
    
    private String businessAddress;
    
    private String businessRegistrationNumber;
    
    private String gstNumber;
    
    private String panNumber;
    
    private String aadharNumber;
    
    private String contactEmail;
    
    private String contactPhone;
    
    private Integer yearsOfExperience;
    
    private List<String> documentUrls;
    
    private RequestStatus status;
    
    private String adminComments;
    
    private UserSummaryDto reviewedBy;
    
    private LocalDateTime reviewedAt;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 