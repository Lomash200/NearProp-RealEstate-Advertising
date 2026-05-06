package com.nearprop.controller.franchisee;

import com.nearprop.dto.ActiveFranchiseeDTO;
import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.franchisee.DistrictPerformanceDto;
import com.nearprop.dto.franchisee.FranchiseeDashboardDto;
import com.nearprop.entity.User;
import com.nearprop.service.franchisee.FranchiseeDashboardService;
import com.nearprop.service.franchisee.FranchiseeDistrictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import com.nearprop.dto.PropertyDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/franchisee/dashboard")
@RequiredArgsConstructor
@Slf4j
public class FranchiseeDashboardController {
    private final FranchiseeDashboardService dashboardService;
    private final FranchiseeDistrictService franchiseeService; // ← NEW

    @GetMapping
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<ApiResponse<FranchiseeDashboardDto>> getDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal User currentUser) {
        log.info("Franchisee {} requesting dashboard from {} to {}", currentUser.getId(), startDate, endDate);
        try {
            FranchiseeDashboardDto dashboardData = dashboardService.getDashboardData(currentUser.getId(), startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved successfully", dashboardData));
        } catch (Exception e) {
            log.error("Failed to get dashboard data for franchisee {}: {}", currentUser.getId(), e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/districts")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<ApiResponse<List<DistrictPerformanceDto>>> getDistrictPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal User currentUser) {
        log.info("Franchisee {} requesting district performance from {} to {}", currentUser.getId(), startDate, endDate);
        try {
            List<DistrictPerformanceDto> districtPerformance = dashboardService.getDistrictPerformance(currentUser.getId(), startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("District performance data retrieved successfully", districtPerformance));
        } catch (Exception e) {
            log.error("Failed to get district performance for franchisee {}: {}", currentUser.getId(), e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/districts/{districtId}")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<ApiResponse<DistrictPerformanceDto>> getSingleDistrictPerformance(
            @PathVariable Long districtId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal User currentUser) {
        log.info("Franchisee {} requesting performance for district {} from {} to {}", currentUser.getId(), districtId, startDate, endDate);
        try {
            DistrictPerformanceDto districtPerformance = dashboardService.getSingleDistrictPerformance(districtId, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("District performance data retrieved successfully", districtPerformance));
        } catch (Exception e) {
            log.error("Failed to get performance for district {}: {}", districtId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Admin endpoints
    @GetMapping("/admin/franchisee/{franchiseeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FranchiseeDashboardDto>> getAdminDashboardForFranchisee(
            @PathVariable Long franchiseeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Admin requesting dashboard for franchisee {} from {} to {}", franchiseeId, startDate, endDate);
        try {
            FranchiseeDashboardDto dashboardData = dashboardService.getDashboardData(franchiseeId, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved successfully", dashboardData));
        } catch (Exception e) {
            log.error("Failed to get dashboard data for franchisee {}: {}", franchiseeId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/admin/franchisee/{franchiseeId}/districts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<DistrictPerformanceDto>>> getAdminDistrictPerformanceForFranchisee(
            @PathVariable Long franchiseeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Admin requesting district performance for franchisee {} from {} to {}", franchiseeId, startDate, endDate);
        try {
            List<DistrictPerformanceDto> districtPerformance = dashboardService.getDistrictPerformance(franchiseeId, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("District performance data retrieved successfully", districtPerformance));
        } catch (Exception e) {
            log.error("Failed to get district performance for franchisee {}: {}", franchiseeId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/admin/districts/{districtId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DistrictPerformanceDto>> getAdminSingleDistrictPerformance(
            @PathVariable Long districtId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Admin requesting performance for district {} from {} to {}", districtId, startDate, endDate);
        try {
            DistrictPerformanceDto districtPerformance = dashboardService.getSingleDistrictPerformance(districtId, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("District performance data retrieved successfully", districtPerformance));
        } catch (Exception e) {
            log.error("Failed to get performance for district {}: {}", districtId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/get-all-district")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<ApiResponse<List<PropertyDto>>> getAllDistrictProperties(
            @AuthenticationPrincipal User currentUser) {
        log.info("Franchisee {} requesting all district properties", currentUser.getId());
        try {
            List<PropertyDto> properties = dashboardService.getAllDistrictProperties(currentUser.getId());
            return ResponseEntity.ok(ApiResponse.success("All properties retrieved successfully", properties));
        } catch (Exception e) {
            log.error("Failed to get properties for franchisee {}: {}", currentUser.getId(), e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/district/summary")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDistrictSummary(
            @AuthenticationPrincipal User currentUser) {
        log.info("Franchisee {} requesting district summary", currentUser.getId());
        try {
            Map<String, Object> summary = dashboardService.getDistrictSummary(currentUser.getId());
            return ResponseEntity.ok(ApiResponse.success("District summary fetched successfully", summary));
        } catch (Exception e) {
            log.error("Failed to get district summary for franchisee {}: {}", currentUser.getId(), e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/franchisee-active")
    public ResponseEntity<ApiResponse<List<ActiveFranchiseeDTO>>> getAllActiveFranchisee() {
        log.info("Public request: Fetching all active franchisees with assigned districts");

        try {
            List<ActiveFranchiseeDTO> list = franchiseeService.getAllActiveFranchisees();

            return ResponseEntity.ok(
                    ApiResponse.success("Active franchisees retrieved successfully", list)
            );
        } catch (Exception e) {
            log.error("Failed to fetch active franchisees: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Internal server error while fetching active franchisees"));
        }
    }

}
