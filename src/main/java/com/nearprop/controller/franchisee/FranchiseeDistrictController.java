package com.nearprop.controller.franchisee;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.franchisee.CreateFranchiseeDistrictDto;
import com.nearprop.dto.franchisee.FranchiseeDistrictDto;
import com.nearprop.dto.franchisee.FranchiseeRevenueDto;
import com.nearprop.dto.franchisee.UpdateFranchiseeDistrictDto;
import com.nearprop.entity.FranchiseeDistrict.FranchiseeStatus;
import com.nearprop.entity.User;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.service.franchisee.FranchiseeDistrictService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nearprop.dto.franchisee.FranchiseeDistrictSubscriptionAnalyticsDto;

@RestController
@RequestMapping("/franchisee/district-assignments")
@RequiredArgsConstructor
@Slf4j
public class FranchiseeDistrictController {
    
    private final FranchiseeDistrictService franchiseeDistrictService;
    
    /**
     * Get all districts assigned to the current franchisee
     */
    @GetMapping("/my-districts")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<List<FranchiseeDistrictDto>> getMyDistricts(
            @AuthenticationPrincipal User currentUser) {
        List<FranchiseeDistrictDto> franchiseeDistricts = 
                franchiseeDistrictService.getFranchiseeDistrictsByUserId(currentUser.getId());
        return ResponseEntity.ok(franchiseeDistricts);
    }
    
    /**
     * Get details of a specific franchisee district assignment
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<FranchiseeDistrictDto> getFranchiseeDistrict(
            @PathVariable("id") Long franchiseeDistrictId) {
        FranchiseeDistrictDto franchiseeDistrict = 
                franchiseeDistrictService.getFranchiseeDistrict(franchiseeDistrictId);
        return ResponseEntity.ok(franchiseeDistrict);
    }
    
    /**
     * Get all franchisee district assignments with pagination (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<FranchiseeDistrictDto>> getAllFranchiseeDistricts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<FranchiseeDistrictDto> franchiseeDistricts = 
                franchiseeDistrictService.getAllFranchiseeDistricts(pageable);
        return ResponseEntity.ok(franchiseeDistricts);
    }
    
    /**
     * Update franchisee district details (Admin only)
     * This endpoint allows updating specific fields of a franchisee district
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FranchiseeDistrictDto> updateFranchiseeDistrict(
            @PathVariable("id") Long franchiseeDistrictId,
            @Valid @RequestBody Map<String, Object> updateFields) {
        try {
            log.info("Updating franchisee district with ID: {}, fields: {}", franchiseeDistrictId, updateFields);
            FranchiseeDistrictDto franchiseeDistrict = 
                    franchiseeDistrictService.updateFranchiseeDistrictFields(franchiseeDistrictId, updateFields);
            return ResponseEntity.ok(franchiseeDistrict);
        } catch (ResourceNotFoundException e) {
            log.error("Failed to update franchisee district: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating franchisee district: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update franchisee district with full DTO (Admin only)
     * This endpoint allows updating all fields of a franchisee district with a single DTO
     */
    @PutMapping("/{id}/full-update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateFranchiseeDistrictFull(
            @PathVariable("id") Long franchiseeDistrictId,
            @Valid @RequestBody CreateFranchiseeDistrictDto updateDto) {
        try {
            log.info("Performing full update of franchisee district with ID: {}", franchiseeDistrictId);
            FranchiseeDistrictDto franchiseeDistrict = 
                    franchiseeDistrictService.updateFranchiseeDistrict(franchiseeDistrictId, updateDto);
            return ResponseEntity.ok(franchiseeDistrict);
        } catch (ResourceNotFoundException e) {
            log.error("Failed to update franchisee district: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Invalid state for franchisee district update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating franchisee district: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }
    
    /**
     * Deactivate a franchisee district assignment (Admin only)
     */
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deactivateFranchiseeDistrict(
            @PathVariable("id") Long franchiseeDistrictId,
            @RequestParam String reason) {
        try {
            log.info("Deactivating franchisee district with ID: {}, reason: {}", franchiseeDistrictId, reason);
            FranchiseeDistrictDto franchiseeDistrict = 
                    franchiseeDistrictService.deactivateFranchiseeDistrict(franchiseeDistrictId, reason);
            return ResponseEntity.ok(franchiseeDistrict);
        } catch (ResourceNotFoundException e) {
            log.error("Failed to deactivate franchisee district: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error deactivating franchisee district: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }
    
    /**
     * Terminate a franchisee district assignment (Admin only)
     * This endpoint completely terminates the franchisee relationship
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> terminateFranchiseeDistrict(
            @PathVariable("id") Long franchiseeDistrictId,
            @RequestParam(required = true) String reason) {
        try {
            log.info("Terminating franchisee district with ID: {}, reason: {}", franchiseeDistrictId, reason);
            franchiseeDistrictService.terminateFranchiseeDistrict(franchiseeDistrictId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Franchisee district terminated successfully");
            response.put("id", franchiseeDistrictId);
            response.put("terminationReason", reason);
            response.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            log.error("Failed to terminate franchisee district: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error terminating franchisee district: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }
    
    /**
     * Get revenue details for a franchisee district
     */
    @GetMapping("/{id}/revenue")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<FranchiseeRevenueDto> getFranchiseeDistrictRevenue(
            @PathVariable("id") Long franchiseeDistrictId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        FranchiseeRevenueDto revenueDetails = 
                franchiseeDistrictService.getFranchiseeRevenue(franchiseeDistrictId, startDate, endDate);
        return ResponseEntity.ok(revenueDetails);
    }
    
    /**
     * Get top performing franchisees
     */
    @GetMapping("/top-performers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FranchiseeDistrictDto>> getTopPerformingFranchisees(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            log.info("Getting top {} performing franchisees", limit);
            List<FranchiseeDistrictDto> topPerformers = franchiseeDistrictService.getTopPerformingFranchisees(limit);
            return ResponseEntity.ok(topPerformers);
        } catch (Exception e) {
            log.error("Error getting top performing franchisees: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get active franchisee for a district
     */
    @GetMapping("/district/{districtId}/active")
    public ResponseEntity<?> getActiveFranchiseeForDistrict(@PathVariable Long districtId) {
        try {
            log.info("Getting active franchisee for district ID: {}", districtId);
            FranchiseeDistrictDto franchiseeDistrict = franchiseeDistrictService.getActiveFranchiseeForDistrict(districtId);
            return ResponseEntity.ok(franchiseeDistrict);
        } catch (ResourceNotFoundException e) {
            log.info("No active franchisee found for district ID: {}", districtId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting active franchisee for district: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }
    
    /**
     * Get franchisee districts by status
     */
    @GetMapping("/by-status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<FranchiseeDistrictDto>> getFranchiseeDistrictsByStatus(
            @PathVariable FranchiseeStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort sort = direction.equalsIgnoreCase("ASC") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<FranchiseeDistrictDto> franchiseeDistricts = 
                franchiseeDistrictService.getFranchiseeDistrictsByStatus(status, pageable);
        return ResponseEntity.ok(franchiseeDistricts);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FranchiseeDistrictDto> createFranchiseeDistrict(@Valid @RequestBody CreateFranchiseeDistrictDto dto) {
        FranchiseeDistrictDto created = franchiseeDistrictService.createFranchiseeDistrict(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/subscriptions")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<ApiResponse<FranchiseeDistrictSubscriptionAnalyticsDto>> getFranchiseeDistrictSubscriptions(@AuthenticationPrincipal User currentUser) {
        FranchiseeDistrictSubscriptionAnalyticsDto analytics = franchiseeDistrictService.getSubscriptionAnalytics(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Franchisee district subscription analytics fetched successfully", analytics));
    }
} 