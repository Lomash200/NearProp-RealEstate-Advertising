package com.nearprop.service.admin;

import com.nearprop.dto.SubscriptionPlanDto;
import com.nearprop.dto.admin.CreateSubscriptionPlanDto;
import com.nearprop.entity.SubscriptionPlan;
import com.nearprop.entity.SubscriptionPlan.PlanType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface SubscriptionPlanAdminService {
    
    /**
     * Create a new subscription plan
     * 
     * @param createDto Plan details
     * @return Created plan
     */
    SubscriptionPlanDto createSubscriptionPlan(CreateSubscriptionPlanDto createDto);
    
    /**
     * Get all subscription plans (paginated)
     * 
     * @param pageable Pagination information
     * @return Page of plans
     */
    Page<SubscriptionPlanDto> getAllSubscriptionPlans(Pageable pageable);


    
    /**
     * Get subscription plan by ID
     * 
     * @param id Plan ID
     * @return Plan details
     */
    SubscriptionPlanDto getSubscriptionPlanById(Long id);
    
    /**
     * Get subscription plans by type
     * 
     * @param type Plan type
     * @return List of plans
     */
    List<SubscriptionPlanDto> getSubscriptionPlansByType(PlanType type);
    
    /**
     * Update a subscription plan
     * 
     * @param id Plan ID
     * @param updateDto Updated plan details
     * @return Updated plan
     */
    SubscriptionPlanDto updateSubscriptionPlan(Long id, CreateSubscriptionPlanDto updateDto);
    
    /**
     * Set the active status of a subscription plan
     * 
     * @param id Plan ID
     * @param active Active status
     * @return Updated plan
     */
    SubscriptionPlanDto setSubscriptionPlanActiveStatus(Long id, boolean active);
    
//    /**
//     * Delete a subscription plan (soft delete by setting active=false)
//     *
////     * @param id Plan ID
//     */
//    Page<SubscriptionPlan> findByActiveTrue(Pageable pageable);
   // Page<SubscriptionPlan> findByActiveTrue(Pageable pageable);
    Page<SubscriptionPlanDto> getAllDeactivatedSubscriptionPlans(Pageable pageable);

    Page<SubscriptionPlanDto> getAllPlansIncludingDeactivated(Pageable pageable);


    void deleteSubscriptionPlan(Long id);
} 