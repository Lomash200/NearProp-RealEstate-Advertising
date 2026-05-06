package com.nearprop.controller.franchisee;

import com.nearprop.dto.franchisee.DistrictRevenueDto;
import com.nearprop.dto.franchisee.FranchiseeRevenueStatsDto;
import com.nearprop.entity.DistrictRevenue.PaymentStatus;
import com.nearprop.entity.DistrictRevenue.RevenueType;
import com.nearprop.service.franchisee.DistrictRevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/franchisee/revenue")
@RequiredArgsConstructor
public class DistrictRevenueController {
    
    private final DistrictRevenueService districtRevenueService;
    
    @PostMapping("/record")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DistrictRevenueDto> recordRevenue(
            @RequestParam Long districtId,
            @RequestParam Long franchiseeDistrictId,
            @RequestParam RevenueType revenueType,
            @RequestParam BigDecimal amount,
            @RequestParam String description,
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) Long subscriptionId) {
        DistrictRevenueDto revenue = districtRevenueService.recordRevenue(
                districtId, franchiseeDistrictId, revenueType, amount, description, propertyId, subscriptionId);
        return new ResponseEntity<>(revenue, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<DistrictRevenueDto> getRevenue(@PathVariable("id") Long revenueId) {
        DistrictRevenueDto revenue = districtRevenueService.getRevenue(revenueId);
        return ResponseEntity.ok(revenue);
    }
    
    @PutMapping("/{id}/payment-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DistrictRevenueDto> updatePaymentStatus(
            @PathVariable("id") Long revenueId,
            @RequestParam PaymentStatus status,
            @RequestParam(required = false) String paymentReference) {
        DistrictRevenueDto revenue = districtRevenueService.updatePaymentStatus(revenueId, status, paymentReference);
        return ResponseEntity.ok(revenue);
    }
    
    @GetMapping("/district/{districtId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<Page<DistrictRevenueDto>> getRevenuesByDistrict(
            @PathVariable Long districtId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DistrictRevenueDto> revenues = districtRevenueService.getRevenuesByDistrict(districtId, pageable);
        return ResponseEntity.ok(revenues);
    }
    
    @GetMapping("/franchisee-district/{franchiseeDistrictId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<Page<DistrictRevenueDto>> getRevenuesByFranchiseeDistrict(
            @PathVariable Long franchiseeDistrictId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DistrictRevenueDto> revenues = districtRevenueService.getRevenuesByFranchiseeDistrict(franchiseeDistrictId, pageable);
        return ResponseEntity.ok(revenues);
    }
    
    @GetMapping("/franchisee/{franchiseeUserId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<Page<DistrictRevenueDto>> getRevenuesByFranchiseeUser(
            @PathVariable Long franchiseeUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DistrictRevenueDto> revenues = districtRevenueService.getRevenuesByFranchiseeUser(franchiseeUserId, pageable);
        return ResponseEntity.ok(revenues);
    }
    
    @GetMapping("/by-date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DistrictRevenueDto>> getRevenuesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DistrictRevenueDto> revenues = districtRevenueService.getRevenuesByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(revenues);
    }
    
    @GetMapping("/by-type/{revenueType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DistrictRevenueDto>> getRevenuesByType(
            @PathVariable RevenueType revenueType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DistrictRevenueDto> revenues = districtRevenueService.getRevenuesByType(revenueType, pageable);
        return ResponseEntity.ok(revenues);
    }
    
    @GetMapping("/by-payment-status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DistrictRevenueDto>> getRevenuesByPaymentStatus(
            @PathVariable PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DistrictRevenueDto> revenues = districtRevenueService.getRevenuesByPaymentStatus(status, pageable);
        return ResponseEntity.ok(revenues);
    }
    
    @GetMapping("/stats/franchisee/{franchiseeUserId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<FranchiseeRevenueStatsDto> getFranchiseeRevenueStats(@PathVariable Long franchiseeUserId) {
        FranchiseeRevenueStatsDto stats = districtRevenueService.getFranchiseeRevenueStats(franchiseeUserId);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/stats/district/{districtId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<FranchiseeRevenueStatsDto> getDistrictRevenueStats(@PathVariable Long districtId) {
        FranchiseeRevenueStatsDto stats = districtRevenueService.getDistrictRevenueStats(districtId);
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/process-payments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> processPendingPayments() {
        districtRevenueService.processPendingPayments();
        return ResponseEntity.noContent().build();
    }
} 