package com.nearprop.service.franchisee;

import com.nearprop.dto.franchisee.MonthlyRevenueReportDto;
import java.util.List;

public interface MockReportGenerationService {
    /**
     * Generates a mock monthly revenue report for a specific franchisee
     * @param franchiseeId The ID of the franchisee
     * @param year The year for the report (defaults to current year if null)
     * @param month The month for the report (defaults to current month if null)
     * @return The generated report as DTO
     */
    MonthlyRevenueReportDto generateMockReportForFranchisee(Long franchiseeId, Integer year, Integer month);
    
    /**
     * Generates mock monthly revenue reports for all franchisees
     * @param year The year for the reports (defaults to current year if null)
     * @param month The month for the reports (defaults to current month if null)
     * @return List of generated reports as DTOs
     */
    List<MonthlyRevenueReportDto> generateMockReportsForAllFranchisees(Integer year, Integer month);
    
    /**
     * Generates a mock monthly revenue report for a specific franchisee district
     * @param franchiseeDistrictId The ID of the franchisee district
     * @param year The year for the report (defaults to current year if null)
     * @param month The month for the report (defaults to current month if null)
     * @return The generated report as DTO
     */
    MonthlyRevenueReportDto generateMockReportForFranchiseeDistrict(Long franchiseeDistrictId, Integer year, Integer month);
    
    /**
     * Generates mock monthly revenue reports for all districts of a franchisee
     * @param franchiseeId The ID of the franchisee
     * @param year The year for the reports (defaults to current year if null)
     * @param month The month for the reports (defaults to current month if null)
     * @return List of generated reports as DTOs
     */
    List<MonthlyRevenueReportDto> generateMockReportsForFranchiseeDistricts(Long franchiseeId, Integer year, Integer month);
} 