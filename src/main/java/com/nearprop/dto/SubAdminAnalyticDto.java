package com.nearprop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubAdminAnalyticDto {

    private long totalProperties;
    private long totalUser;
    private long totalReel;
    private long totalAdvertisement;
    private long totalSeller;
    private long totalAdvisor;
    private long totalSubAdmin;
    private long totalFranchisee;
    private long totalDeveloper;
    private long totalAdmin;

}