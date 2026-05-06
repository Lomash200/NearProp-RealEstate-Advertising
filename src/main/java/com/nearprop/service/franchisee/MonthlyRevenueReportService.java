package com.nearprop.service.franchisee;

import com.nearprop.dto.franchisee.MonthlyRevenueReportDto;
import com.nearprop.entity.MonthlyRevenueReport.ReportStatus;

import java.time.LocalDate;
import java.util.List;

public interface MonthlyRevenueReportService {
    
    /**
     * Generate monthly revenue reports for all franchisees
     * This should be run at the end of each month
     * 
     * @return Number of reports generated
     */
    int generateMonthlyReports();
    
    /**
     * Get a specific monthly revenue report by ID
     * 
     * @param reportId Report ID
     * @return Monthly revenue report
     */
    MonthlyRevenueReportDto getReportById(Long reportId);
    
    /**
     * Get all monthly revenue reports for a specific franchisee
     * 
     * @param franchiseeId Franchisee ID
     * @return List of monthly revenue reports
     */
    List<MonthlyRevenueReportDto> getReportsByFranchisee(Long franchiseeId);
    
    /**
     * Get all monthly revenue reports for a specific franchisee district
     * 
     * @param franchiseeDistrictId Franchisee district ID
     * @return List of monthly revenue reports
     */
    List<MonthlyRevenueReportDto> getReportsByFranchiseeDistrict(Long franchiseeDistrictId);
    
    /**
     * Get all monthly revenue reports for a specific year and month
     * 
     * @param year Year
     * @param month Month
     * @return List of monthly revenue reports
     */
    List<MonthlyRevenueReportDto> getReportsByYearAndMonth(Integer year, Integer month);
    
    /**
     * Get all monthly revenue reports by status
     * 
     * @param status Report status
     * @return List of monthly revenue reports
     */
    List<MonthlyRevenueReportDto> getReportsByStatus(ReportStatus status);
    
    /**
     * Process a monthly revenue report (mark as paid or cancelled)
     * 
     * @param reportId Report ID
     * @param status New status
     * @param adminComments Admin comments
     * @param paymentReference Payment reference number
     * @param paymentMethod Payment method
     * @param paymentDate Payment date
     * @param transactionType Type of transaction
     * @param transactionId Transaction ID 
     * @param accountNumber Account number for payment
     * @param ifscCode IFSC code for payment
     * @param bankName Bank name
     * @param proofUrl URL of payment proof document
     * @param adminId Admin ID
     * @return Updated report
     */
    MonthlyRevenueReportDto processReport(Long reportId, ReportStatus status, String adminComments,
                                     String paymentReference, String paymentMethod, LocalDate paymentDate,
                                     String transactionType, String transactionId, String accountNumber,
                                     String ifscCode, String bankName, String proofUrl, Long adminId);
    
    /**
     * Get summary of reports for a franchisee
     * 
     * @param franchiseeId Franchisee ID
     * @return List of reports
     */
    List<MonthlyRevenueReportDto> getReportSummaryForFranchisee(Long franchiseeId);
    
    /**
     * Get summary of reports for a franchisee district
     * 
     * @param franchiseeDistrictId Franchisee district ID
     * @return List of reports
     */
    List<MonthlyRevenueReportDto> getReportSummaryForFranchiseeDistrict(Long franchiseeDistrictId);
    
    /**
     * Get summary of reports for all franchisees
     * 
     * @return List of reports
     */
    List<MonthlyRevenueReportDto> getReportSummaryForAllFranchisees();
} 