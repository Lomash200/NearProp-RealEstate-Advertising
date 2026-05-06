package com.nearprop.controller.admin;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.franchisee.FranchiseeDistrictDto;
import com.nearprop.dto.franchisee.MonthlyRevenueReportDto;
import com.nearprop.entity.MonthlyRevenueReport.ReportStatus;
import com.nearprop.service.franchisee.FranchiseeDistrictService;
import com.nearprop.service.franchisee.MonthlyRevenueReportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/franchisee")
@RequiredArgsConstructor
@Slf4j
public class FranchiseeAdminController {

    private final FranchiseeDistrictService franchiseeDistrictService;
    private final MonthlyRevenueReportService monthlyRevenueReportService;
    
    /**
     * Delete a franchisee completely (Admin only)
     * This will remove the franchisee role and all associated data
     */
    @DeleteMapping("/{franchiseeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteFranchisee(
            @PathVariable Long franchiseeId,
            @RequestParam(required = true) String reason) {
        
        log.info("Admin request to delete franchisee with ID: {}, reason: {}", franchiseeId, reason);
        
        try {
            Map<String, Object> result = franchiseeDistrictService.deleteFranchisee(franchiseeId, reason);
            
            return ResponseEntity.ok(ApiResponse.success("Franchisee deleted successfully", result));
        } catch (Exception e) {
            log.error("Error deleting franchisee with ID: {}", franchiseeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete franchisee: " + e.getMessage()));
        }
    }
    
    /**
     * Get all monthly revenue reports for a specific franchisee
     */
    @GetMapping("/{franchiseeId}/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MonthlyRevenueReportDto>> getFranchiseeReports(
            @PathVariable Long franchiseeId) {
        
        log.info("Getting monthly revenue reports for franchisee: {}", franchiseeId);
        return ResponseEntity.ok(monthlyRevenueReportService.getReportsByFranchisee(franchiseeId));
    }
    
    /**
     * Get all pending monthly revenue reports
     */
    @GetMapping("/reports/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MonthlyRevenueReportDto>> getPendingReports() {
        log.info("Getting all pending monthly revenue reports");
        return ResponseEntity.ok(monthlyRevenueReportService.getReportsByStatus(ReportStatus.PENDING));
    }
    
    /**
     * Process a monthly revenue report (mark as paid)
     */
    @PutMapping("/reports/{reportId}/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MonthlyRevenueReportDto> processReport(
            @PathVariable Long reportId,
            @RequestBody Map<String, Object> requestBody,
            @RequestParam(required = false) Long adminId) {
        
        String paymentReference = (String) requestBody.get("paymentReference");
        String paymentMethod = (String) requestBody.get("paymentMethod");
        String status = (String) requestBody.get("status");
        String adminComments = (String) requestBody.get("comments");
        String transactionType = (String) requestBody.get("transactionType");
        String transactionId = (String) requestBody.get("transactionId");
        String accountNumber = (String) requestBody.get("accountNumber");
        String ifscCode = (String) requestBody.get("ifscCode");
        String bankName = (String) requestBody.get("bankName");
        String proofUrl = (String) requestBody.get("proofUrl");
        
        // Parse date if provided
        LocalDate paymentDate = null;
        if (requestBody.get("paymentDate") != null) {
            paymentDate = LocalDate.parse((String) requestBody.get("paymentDate"));
        } else {
            paymentDate = LocalDate.now(); // Default to today
        }
        
        // Use current admin ID if not provided
        Long processingAdminId = adminId != null ? adminId : 1L; // Default admin ID
        
        log.info("Processing monthly revenue report: {}, status: {}", reportId, status);
        
        MonthlyRevenueReportDto updatedReport = monthlyRevenueReportService.processReport(
                reportId, 
                ReportStatus.valueOf(status.toUpperCase()), 
                adminComments,
                paymentReference, 
                paymentMethod,
                paymentDate,
                transactionType,
                transactionId,
                accountNumber,
                ifscCode,
                bankName,
                proofUrl,
                processingAdminId);
        
        return ResponseEntity.ok(updatedReport);
    }
    
    /**
     * Generate monthly revenue reports manually
     */
    @PostMapping("/reports/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> generateReports() {
        log.info("Manual request to generate monthly revenue reports");
        
        int generated = monthlyRevenueReportService.generateMonthlyReports();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("reportsGenerated", generated);
        response.put("timestamp", java.time.LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
} 