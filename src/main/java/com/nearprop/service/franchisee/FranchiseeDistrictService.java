package com.nearprop.service.franchisee;

import com.nearprop.dto.ActiveFranchiseeDTO;
import com.nearprop.dto.franchisee.CreateFranchiseeDistrictDto;
import com.nearprop.dto.franchisee.FranchiseeDistrictDto;
import com.nearprop.dto.franchisee.FranchiseeRevenueDto;
import com.nearprop.dto.franchisee.FranchiseeDistrictSubscriptionAnalyticsDto;
import com.nearprop.entity.FranchiseeDistrict.FranchiseeStatus;
import com.nearprop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FranchiseeDistrictService {
    
    /**
     * Assign a franchisee to a district
     * 
     * @param createDto Franchisee district assignment data
     * @return Created franchisee district assignment
     */
    FranchiseeDistrictDto assignFranchiseeToDistrict(CreateFranchiseeDistrictDto createDto);
    
    /**
     * Get a franchisee district assignment by ID
     * 
     * @param franchiseeDistrictId Franchisee district ID
     * @return Franchisee district details
     */
    FranchiseeDistrictDto getFranchiseeDistrict(Long franchiseeDistrictId);
    
    /**
     * Update a franchisee district assignment
     * 
     * @param franchiseeDistrictId Franchisee district ID
     * @param updateDto Franchisee district update data
     * @return Updated franchisee district
     */
    FranchiseeDistrictDto updateFranchiseeDistrict(Long franchiseeDistrictId, CreateFranchiseeDistrictDto updateDto);
    
    /**
     * Terminate a franchisee district assignment
     * 
     * @param franchiseeDistrictId Franchisee district ID
     */
    void terminateFranchiseeDistrict(Long franchiseeDistrictId);
    
    /**
     * Get all franchisee district assignments with pagination
     * 
     * @param pageable Pagination information
     * @return Page of franchisee districts
     */
    Page<FranchiseeDistrictDto> getAllFranchiseeDistricts(Pageable pageable);
    
    /**
     * Get franchisee districts by franchisee user ID
     * 
     * @param franchiseeUserId Franchisee user ID
     * @return List of franchisee district assignments
     */
    List<FranchiseeDistrictDto> getFranchiseeDistrictsByUserId(Long franchiseeUserId);
    
    /**
     * Get franchisee districts by district ID
     * 
     * @param districtId District ID
     * @return List of franchisee district assignments
     */
    List<FranchiseeDistrictDto> getFranchiseeDistrictsByDistrictId(Long districtId);
    
    /**
     * Get franchisee districts by status
     * 
     * @param status Franchisee status
     * @param pageable Pagination information
     * @return Page of franchisee districts
     */
    Page<FranchiseeDistrictDto> getFranchiseeDistrictsByStatus(FranchiseeStatus status, Pageable pageable);
    
    /**
     * Get active franchisee for a district
     * 
     * @param districtId District ID
     * @return Franchisee district assignment if exists
     */
    FranchiseeDistrictDto getActiveFranchiseeForDistrict(Long districtId);
    
    /**
     * Get top performing franchisees
     * 
     * @param limit Number of franchisees to return
     * @return List of top performing franchisee districts
     */
    List<FranchiseeDistrictDto> getTopPerformingFranchisees(int limit);
    
    /**
     * Update franchisee performance metrics
     * 
     * @param franchiseeDistrictId Franchisee district ID
     */
    void updateFranchiseePerformanceMetrics(Long franchiseeDistrictId);
    
    /**
     * Process expired franchisee assignments
     */
    void processExpiredFranchiseeAssignments();
    
    /**
     * Update specific fields of a franchisee district
     * 
     * @param franchiseeDistrictId Franchisee district ID
     * @param updateFields Map of field names and new values
     * @return Updated franchisee district
     */
    FranchiseeDistrictDto updateFranchiseeDistrictFields(Long franchiseeDistrictId, Map<String, Object> updateFields);
    
    /**
     * Deactivate a franchisee district assignment
     * 
     * @param franchiseeDistrictId Franchisee district ID
     * @param reason Reason for deactivation
     * @return Updated franchisee district
     */
    FranchiseeDistrictDto deactivateFranchiseeDistrict(Long franchiseeDistrictId, String reason);

    /**
     * Delete a franchisee completely (Admin only)
     * This will remove the franchisee role and all associated data
     * 
     * @param franchiseeId Franchisee user ID
     * @param reason Reason for deletion
     * @return Map with deletion results
     */
    Map<String, Object> deleteFranchisee(Long franchiseeId, String reason);
    
    /**
     * Get revenue details for a franchisee district
     * 
     * @param franchiseeDistrictId Franchisee district ID
     * @param startDate Start date for revenue period
     * @param endDate End date for revenue period
     * @return Revenue details
     */
    FranchiseeRevenueDto getFranchiseeRevenue(Long franchiseeDistrictId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get subscription analytics for a franchisee's district(s)
     * 
     * @param franchiseeUser Franchisee user
     * @return Subscription analytics
     */
    FranchiseeDistrictSubscriptionAnalyticsDto getSubscriptionAnalytics(User franchiseeUser);

    FranchiseeDistrictDto createFranchiseeDistrict(CreateFranchiseeDistrictDto dto);

    /**
     * Get a franchisee district by ID
     * 
     * @param districtId Franchisee district ID
     * @return Franchisee district entity
     */
    com.nearprop.entity.FranchiseeDistrict getDistrictById(Long districtId);

    List<ActiveFranchiseeDTO> getAllActiveFranchisees();
} 