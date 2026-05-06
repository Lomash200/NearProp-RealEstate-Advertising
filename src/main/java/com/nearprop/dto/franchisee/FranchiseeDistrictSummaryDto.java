package com.nearprop.dto.franchisee;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FranchiseeDistrictSummaryDto {
    
    private Long id;
    
    private Long districtId;
    
    private String districtName;
    
    private String state;
    
    private boolean active;
} 