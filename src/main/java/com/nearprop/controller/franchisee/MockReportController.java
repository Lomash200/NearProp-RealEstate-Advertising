package com.nearprop.controller.franchisee;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.franchisee.MonthlyRevenueReportDto;
import com.nearprop.entity.User;
import com.nearprop.service.franchisee.MockReportGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mock-reports")
@RequiredArgsConstructor
@Slf4j
public class MockReportController {
    private final MockReportGenerationService mockReportService;

    @PostMapping("/franchisee/{franchiseeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MonthlyRevenueReportDto>> generateMockReportForFranchisee(
            @PathVariable Long franchiseeId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        log.info("Admin generating mock report for franchisee {} for {}/{}", franchiseeId, year, month);
        try {
            MonthlyRevenueReportDto report = mockReportService.generateMockReportForFranchisee(franchiseeId, year, month);
            return ResponseEntity.ok(ApiResponse.success("Mock report generated successfully", report));
        } catch (Exception e) {
            log.error("Failed to generate mock report for franchisee {}: {}", franchiseeId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/all-franchisees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<MonthlyRevenueReportDto>>> generateMockReportsForAllFranchisees(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        log.info("Admin generating mock reports for all franchisees for {}/{}", year, month);
        try {
            List<MonthlyRevenueReportDto> reports = mockReportService.generateMockReportsForAllFranchisees(year, month);
            return ResponseEntity.ok(ApiResponse.success("Mock reports generated successfully for all franchisees", reports));
        } catch (Exception e) {
            log.error("Failed to generate mock reports for all franchisees: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/district/{districtId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<ApiResponse<MonthlyRevenueReportDto>> generateMockReportForDistrict(
            @PathVariable Long districtId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        log.info("Generating mock report for district {} for {}/{}", districtId, year, month);
        try {
            MonthlyRevenueReportDto report = mockReportService.generateMockReportForFranchiseeDistrict(districtId, year, month);
            return ResponseEntity.ok(ApiResponse.success("Mock report generated successfully for district", report));
        } catch (Exception e) {
            log.error("Failed to generate mock report for district {}: {}", districtId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/my-districts")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<ApiResponse<List<MonthlyRevenueReportDto>>> generateMockReportsForMyDistricts(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal User currentUser) {
        Long franchiseeId = currentUser.getId();
        log.info("Franchisee {} generating mock reports for their districts for {}/{}", franchiseeId, year, month);
        try {
            List<MonthlyRevenueReportDto> reports = mockReportService.generateMockReportsForFranchiseeDistricts(franchiseeId, year, month);
            return ResponseEntity.ok(ApiResponse.success("Mock reports generated successfully for your districts", reports));
        } catch (Exception e) {
            log.error("Failed to generate mock reports for franchisee {} districts: {}", franchiseeId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/franchisee/{franchiseeId}/districts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<MonthlyRevenueReportDto>>> generateMockReportsForFranchiseeDistricts(
            @PathVariable Long franchiseeId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        log.info("Admin generating mock reports for franchisee {} districts for {}/{}", franchiseeId, year, month);
        try {
            List<MonthlyRevenueReportDto> reports = mockReportService.generateMockReportsForFranchiseeDistricts(franchiseeId, year, month);
            return ResponseEntity.ok(ApiResponse.success("Mock reports generated successfully for franchisee districts", reports));
        } catch (Exception e) {
            log.error("Failed to generate mock reports for franchisee {} districts: {}", franchiseeId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 