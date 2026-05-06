package com.nearprop.controller.franchisee;

import com.nearprop.dto.franchisee.MonthlyRevenueReportDto;
import com.nearprop.entity.MonthlyRevenueReport.ReportStatus;
import com.nearprop.entity.User;
import com.nearprop.service.franchisee.MonthlyRevenueReportService;
import com.nearprop.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/franchisee/reports")
@RequiredArgsConstructor
@Slf4j
public class MonthlyRevenueReportController {

    private final MonthlyRevenueReportService revenueReportService;
    private final S3Service s3Service;

    @GetMapping("/{reportId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<MonthlyRevenueReportDto> getReport(@PathVariable Long reportId) {
        log.info("Getting monthly revenue report with ID: {}", reportId);
        MonthlyRevenueReportDto report = revenueReportService.getReportById(reportId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<List<MonthlyRevenueReportDto>> getMyReports(
            @AuthenticationPrincipal User currentUser) {
        log.info("Getting monthly revenue reports for franchisee: {}", currentUser.getId());
        List<MonthlyRevenueReportDto> reports = revenueReportService.getReportsByFranchisee(currentUser.getId());
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/district/{districtId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<List<MonthlyRevenueReportDto>> getReportsByDistrict(
            @PathVariable Long districtId) {
        log.info("Getting monthly revenue reports for district: {}", districtId);
        List<MonthlyRevenueReportDto> reports = revenueReportService.getReportsByFranchiseeDistrict(districtId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MonthlyRevenueReportDto>> getAllReports() {
        log.info("Admin getting all monthly revenue reports");
        List<MonthlyRevenueReportDto> reports = revenueReportService.getReportsByYearAndMonth(null, null);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/admin/franchisee/{franchiseeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MonthlyRevenueReportDto>> getReportsByFranchisee(
            @PathVariable Long franchiseeId) {
        log.info("Admin getting monthly revenue reports for franchisee: {}", franchiseeId);
        List<MonthlyRevenueReportDto> reports = revenueReportService.getReportsByFranchisee(franchiseeId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MonthlyRevenueReportDto>> getReportsByStatus(
            @PathVariable ReportStatus status) {
        log.info("Admin getting monthly revenue reports with status: {}", status);
        List<MonthlyRevenueReportDto> reports = revenueReportService.getReportsByStatus(status);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/admin/date")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MonthlyRevenueReportDto>> getReportsByDate(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        log.info("Admin getting monthly revenue reports for {}/{}", year, month);
        List<MonthlyRevenueReportDto> reports = revenueReportService.getReportsByYearAndMonth(year, month);
        return ResponseEntity.ok(reports);
    }

    @PostMapping("/admin/process/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MonthlyRevenueReportDto> processReport(
            @PathVariable Long reportId,
            @RequestParam ReportStatus status,
            @RequestParam(required = false) String adminComments,
            @RequestParam(required = false) String paymentReference,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) String ifscCode,
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) MultipartFile paymentProof,
            @AuthenticationPrincipal User currentUser) {
        
        log.info("Admin processing monthly revenue report: {} with status: {}", reportId, status);
        
        String proofUrl = null;
        if (paymentProof != null && !paymentProof.isEmpty()) {
            // Save payment proof to S3
            proofUrl = s3Service.uploadMonthlyReportProof(reportId, paymentProof);
            log.info("Payment proof uploaded for report {}: {}", reportId, proofUrl);
        }
        
        MonthlyRevenueReportDto report = revenueReportService.processReport(
                reportId, status, adminComments, paymentReference, paymentMethod, paymentDate,
                transactionType, transactionId, accountNumber, ifscCode, bankName, proofUrl,
                currentUser.getId());
        
        return ResponseEntity.ok(report);
    }

    @PostMapping("/admin/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> generateMonthlyReports() {
        log.info("Admin generating monthly revenue reports");
        int count = revenueReportService.generateMonthlyReports();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<List<MonthlyRevenueReportDto>> getReportSummary(
            @AuthenticationPrincipal User currentUser) {
        log.info("Getting monthly revenue report summary for franchisee: {}", currentUser.getId());
        List<MonthlyRevenueReportDto> summary = revenueReportService.getReportSummaryForFranchisee(
                currentUser.getId());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/admin/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MonthlyRevenueReportDto>> getAllReportSummary() {
        log.info("Admin getting monthly revenue report summary for all franchisees");
        List<MonthlyRevenueReportDto> summary = revenueReportService.getReportSummaryForAllFranchisees();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/admin/summary/franchisee/{franchiseeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MonthlyRevenueReportDto>> getFranchiseeReportSummary(
            @PathVariable Long franchiseeId) {
        log.info("Admin getting monthly revenue report summary for franchisee: {}", franchiseeId);
        List<MonthlyRevenueReportDto> summary = revenueReportService.getReportSummaryForFranchisee(
                franchiseeId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/admin/summary/district/{districtId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MonthlyRevenueReportDto>> getDistrictReportSummary(
            @PathVariable Long districtId) {
        log.info("Admin getting monthly revenue report summary for district: {}", districtId);
        List<MonthlyRevenueReportDto> summary = revenueReportService.getReportSummaryForFranchiseeDistrict(
                districtId);
        return ResponseEntity.ok(summary);
    }
} 