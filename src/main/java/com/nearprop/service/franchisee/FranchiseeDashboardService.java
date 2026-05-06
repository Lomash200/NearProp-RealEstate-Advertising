package com.nearprop.service.franchisee;

import com.nearprop.dto.franchisee.DistrictPerformanceDto;
import com.nearprop.dto.franchisee.FranchiseeDashboardDto;
import com.nearprop.dto.PropertyDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface for franchisee dashboard data
 */
public interface FranchiseeDashboardService {
    /**
     * Get comprehensive dashboard data for a franchisee
     * @param franchiseeId ID of the franchisee
     * @param startDate Optional start date for filtering data (defaults to 30 days ago if null)
     * @param endDate Optional end date for filtering data (defaults to current date if null)
     * @return Dashboard data
     */
    FranchiseeDashboardDto getDashboardData(Long franchiseeId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get performance metrics for all districts of a franchisee
     * @param franchiseeId ID of the franchisee
     * @param startDate Optional start date for filtering data (defaults to 30 days ago if null)
     * @param endDate Optional end date for filtering data (defaults to current date if null)
     * @return List of district performance metrics
     */
    List<DistrictPerformanceDto> getDistrictPerformance(Long franchiseeId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get performance metrics for a specific district
     * @param districtId ID of the district
     * @param startDate Optional start date for filtering data (defaults to 30 days ago if null)
     * @param endDate Optional end date for filtering data (defaults to current date if null)
     * @return District performance metrics
     */
    DistrictPerformanceDto getSingleDistrictPerformance(Long districtId, LocalDate startDate, LocalDate endDate);


    List<PropertyDto> getAllDistrictProperties(Long id);

    Map<String, Object> getDistrictSummary(Long franchiseeId);


} 
