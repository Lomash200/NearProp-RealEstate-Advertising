package com.nearprop.controller.admin;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.SubscriptionPlanDto;
import com.nearprop.dto.admin.CreateSubscriptionPlanDto;
import com.nearprop.entity.SubscriptionPlan.PlanType;
import com.nearprop.service.admin.SubscriptionPlanAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/subscription-plans")
@RequiredArgsConstructor
@Slf4j
//@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUBADMIN')")


public class SubscriptionPlanAdminController {

    private final SubscriptionPlanAdminService planAdminService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionPlanDto>> createPlan(
            @Valid @RequestBody CreateSubscriptionPlanDto createDto) {
        log.info("Creating new subscription plan with name: {}", createDto.getName());
        SubscriptionPlanDto planDto = planAdminService.createSubscriptionPlan(createDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subscription plan created successfully", planDto));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SubscriptionPlanDto>>> getAllPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        log.info("Fetching all subscription plans");
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubscriptionPlanDto> plans = planAdminService.getAllSubscriptionPlans(pageable);
        return ResponseEntity.ok(ApiResponse.success("Subscription plans retrieved successfully", plans));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<SubscriptionPlanDto>>> getAllPlansIncludingDeactivated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubscriptionPlanDto> plans =
                planAdminService.getAllPlansIncludingDeactivated(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("All subscription plans fetched successfully", plans)
        );
    }


    @GetMapping("/deactivated")
    public ResponseEntity<ApiResponse<Page<SubscriptionPlanDto>>> getAllDeactivatedPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<SubscriptionPlanDto> plans =
                planAdminService.getAllDeactivatedSubscriptionPlans(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Deactivated subscription plans fetched successfully", plans)
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanDto>> getPlanById(@PathVariable Long id) {
        log.info("Fetching subscription plan with ID: {}", id);
        SubscriptionPlanDto planDto = planAdminService.getSubscriptionPlanById(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription plan retrieved successfully", planDto));
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<SubscriptionPlanDto>>> getPlansByType(@PathVariable PlanType type) {
        log.info("Fetching subscription plans of type: {}", type);
        List<SubscriptionPlanDto> plans = planAdminService.getSubscriptionPlansByType(type);
        return ResponseEntity.ok(ApiResponse.success("Subscription plans retrieved successfully", plans));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanDto>> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody CreateSubscriptionPlanDto updateDto) {
        log.info("Updating subscription plan with ID: {}", id);
        SubscriptionPlanDto planDto = planAdminService.updateSubscriptionPlan(id, updateDto);
        return ResponseEntity.ok(ApiResponse.success("Subscription plan updated successfully", planDto));
    }
    
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<SubscriptionPlanDto>> activatePlan(@PathVariable Long id) {
        log.info("Activating subscription plan with ID: {}", id);
        SubscriptionPlanDto planDto = planAdminService.setSubscriptionPlanActiveStatus(id, true);
        return ResponseEntity.ok(ApiResponse.success("Subscription plan activated successfully", planDto));
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<SubscriptionPlanDto>> deactivatePlan(@PathVariable Long id) {
        log.info("Deactivating subscription plan with ID: {}", id);
        SubscriptionPlanDto planDto = planAdminService.setSubscriptionPlanActiveStatus(id, false);
        return ResponseEntity.ok(ApiResponse.success("Subscription plan deactivated successfully", planDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePlan(@PathVariable Long id) {
        log.info("Deleting subscription plan with ID: {}", id);
        planAdminService.deleteSubscriptionPlan(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription plan deleted successfully"));
    }
} 